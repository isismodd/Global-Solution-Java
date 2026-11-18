package br.com.fiap.conectasocial.repository;

import br.com.fiap.conectasocial.model.CampanhaInteresse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.util.Map;

import java.util.List;

@Repository
public class CampanhaInteresseRepository {

    private final JdbcTemplate jdbc;

    public CampanhaInteresseRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<CampanhaInteresse> rowMapper = (rs, rowNum) -> {
        CampanhaInteresse interesse = new CampanhaInteresse();

        interesse.setId(rs.getLong("ID"));
        interesse.setCampanhaId(rs.getLong("CAMPANHA_ID"));
        interesse.setUsuarioId(rs.getLong("USUARIO_ID"));
        interesse.setTipoInteresse(rs.getString("TIPO_INTERESSE"));
        interesse.setDataInteresse(
                rs.getTimestamp("DATA_INTERESSE").toLocalDateTime()
        );
        interesse.setNomeUsuario(rs.getString("NOME_USUARIO"));
        interesse.setEmailUsuario(rs.getString("EMAIL_USUARIO"));
        interesse.setTituloCampanha(rs.getString("TITULO_CAMPANHA"));

        return interesse;
    };

    public void inserir(Long campanhaId, Long usuarioId, String tipoInteresse) {
        String sql = """
                INSERT INTO INTERESSES_CAMPANHAS (
                    CAMPANHA_ID,
                    USUARIO_ID,
                    TIPO_INTERESSE,
                    DATA_INTERESSE
                ) VALUES (?, ?, ?, SYSTIMESTAMP)
                """;

        jdbc.update(sql, campanhaId, usuarioId, tipoInteresse);
    }


    public boolean jaDemonstrouInteresse(Long campanhaId, Long usuarioId) {
        String sql = """
            SELECT COUNT(*)
            FROM INTERESSES_CAMPANHAS
            WHERE CAMPANHA_ID = ?
              AND USUARIO_ID = ?
            """;

        Integer quantidade = jdbc.queryForObject(
                sql,
                Integer.class,
                campanhaId,
                usuarioId
        );

        return quantidade != null && quantidade > 0;
    }


    public List<CampanhaInteresse> listarPorOng(Long ongId, String tipoInteresse) {
        String sql = """
                SELECT
                    IC.ID,
                    IC.CAMPANHA_ID,
                    IC.USUARIO_ID,
                    IC.TIPO_INTERESSE,
                    IC.DATA_INTERESSE,
                    U.NOME AS NOME_USUARIO,
                    U.EMAIL AS EMAIL_USUARIO,
                    C.TITULO AS TITULO_CAMPANHA
                FROM INTERESSES_CAMPANHAS IC
                INNER JOIN USUARIOS U ON U.ID = IC.USUARIO_ID
                INNER JOIN CAMPANHAS C ON C.ID = IC.CAMPANHA_ID
                WHERE C.ONG_ID = ?
                  AND IC.TIPO_INTERESSE = ?
                ORDER BY IC.DATA_INTERESSE DESC
                """;

        return jdbc.query(sql, rowMapper, ongId, tipoInteresse);
    }

    public int excluirPorCampanha(Long campanhaId) {
        String sql = """
            DELETE FROM INTERESSES_CAMPANHAS
            WHERE CAMPANHA_ID = ?
            """;

        return jdbc.update(sql, campanhaId);
    }

    public Map<String, Object> buscarDadosNotificacao(Long campanhaId, Long usuarioId) {
        String sql = """
        SELECT
            IC.ID AS INTERESSE_ID,
            IC.CAMPANHA_ID,
            C.TITULO AS TITULO_CAMPANHA,
            U_ONG.NOME AS NOME_ONG,
            U_ONG.EMAIL AS EMAIL_ONG,
            IC.USUARIO_ID,
            U_INT.NOME AS NOME_USUARIO,
            IC.TIPO_INTERESSE,
            IC.DATA_INTERESSE
        FROM INTERESSES_CAMPANHAS IC
        INNER JOIN CAMPANHAS C ON C.ID = IC.CAMPANHA_ID
        INNER JOIN ONGS O ON O.ID = C.ONG_ID
        INNER JOIN USUARIOS U_ONG ON U_ONG.ID = O.USUARIO_ID
        INNER JOIN USUARIOS U_INT ON U_INT.ID = IC.USUARIO_ID
        WHERE IC.CAMPANHA_ID = ?
          AND IC.USUARIO_ID = ?
        """;

        return jdbc.queryForMap(sql, campanhaId, usuarioId);
    }
}