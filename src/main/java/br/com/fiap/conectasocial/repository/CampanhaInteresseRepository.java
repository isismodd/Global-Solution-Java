package br.com.fiap.conectasocial.repository;

import br.com.fiap.conectasocial.model.CampanhaInteresse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

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
}