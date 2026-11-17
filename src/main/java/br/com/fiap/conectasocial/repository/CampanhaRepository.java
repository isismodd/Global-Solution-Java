
package br.com.fiap.conectasocial.repository;

import br.com.fiap.conectasocial.model.Campanha;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class CampanhaRepository {

    private final JdbcTemplate jdbc;

    public CampanhaRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Campanha> rowMapper = (rs, rowNum) -> {
        Campanha campanha = new Campanha();

        campanha.setId(rs.getLong("ID"));
        campanha.setOngId(rs.getLong("ONG_ID"));
        campanha.setTitulo(rs.getString("TITULO"));
        campanha.setDescricao(rs.getString("DESCRICAO"));
        campanha.setCategoria(rs.getString("CATEGORIA"));
        campanha.setLocal(rs.getString("LOCAL"));
        campanha.setStatus(rs.getString("STATUS"));

        Timestamp data = rs.getTimestamp("DATA_CRIACAO");
        if (data != null) {
            campanha.setDataCriacao(data.toLocalDateTime());
        }

        return campanha;
    };

    public List<Campanha> listarTodas() {
        String sql = """
            SELECT * FROM CAMPANHAS
            ORDER BY DATA_CRIACAO DESC
            """;

        return jdbc.query(sql, rowMapper);
    }

    public List<Campanha> listarPorOng(Long ongId) {
        String sql = """
            SELECT * FROM CAMPANHAS
            WHERE ONG_ID = ?
            ORDER BY DATA_CRIACAO DESC
            """;

        return jdbc.query(sql, rowMapper, ongId);
    }

    public Optional<Long> buscarIdOngPorUsuario(Long usuarioId) {
        String sql = """
            SELECT ID FROM ONGS
            WHERE USUARIO_ID = ?
            """;

        return jdbc.query(sql, rs ->
                        rs.next() ? Optional.of(rs.getLong("ID")) : Optional.empty(),
                usuarioId
        );
    }

    public void inserir(Campanha campanha, Long ongId) {
        String sql = """
        INSERT INTO CAMPANHAS (
            ONG_ID,
            TITULO,
            DESCRICAO,
            CATEGORIA,
            LOCAL,
            STATUS,
            DATA_CRIACAO
        ) VALUES (?, ?, ?, ?, ?, 'ATIVA', SYSTIMESTAMP)
        """;

        jdbc.update(
                sql,
                ongId,
                campanha.getTitulo().trim(),
                campanha.getDescricao().trim(),
                campanha.getCategoria().trim(),
                campanha.getLocal().trim()
        );
    }

    public List<Map<String, Object>> listarCampanhasDoFeed() {
        String sql = """
        SELECT
            C.ID,
            C.TITULO,
            C.DESCRICAO,
            C.CATEGORIA,
            C.LOCAL,
            C.ONG_ID,
            U.NOME AS NOME_ONG
        FROM CAMPANHAS C
        INNER JOIN ONGS O ON O.ID = C.ONG_ID
        INNER JOIN USUARIOS U ON U.ID = O.USUARIO_ID
        WHERE C.STATUS = 'ATIVA'
        ORDER BY C.DATA_CRIACAO DESC
        """;

        return jdbc.queryForList(sql);
    }
}
