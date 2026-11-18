
package br.com.fiap.conectasocial.repository;

import br.com.fiap.conectasocial.model.Campanha;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.io.ByteArrayInputStream;
import java.sql.PreparedStatement;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.sql.Blob;

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

    public boolean possuiImagemCampanha(Long id) {
        Boolean possuiImagem = jdbc.queryForObject(
                "SELECT CASE WHEN IMAGEM IS NOT NULL THEN 1 ELSE 0 END FROM CAMPANHAS WHERE ID = ?",
                Boolean.class,
                id
        );

        return Boolean.TRUE.equals(possuiImagem);
    }

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

    public void inserir(Campanha campanha, Long ongId, byte[] imagem) {

        String sql = """
        INSERT INTO CAMPANHAS (
            ONG_ID,
            TITULO,
            DESCRICAO,
            CATEGORIA,
            LOCAL,
            STATUS,
            DATA_CRIACAO,
            IMAGEM
        ) VALUES (?, ?, ?, ?, ?, 'ATIVA', SYSTIMESTAMP, ?)
        """;

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setLong(1, ongId);
            ps.setString(2, campanha.getTitulo().trim());
            ps.setString(3, campanha.getDescricao().trim());
            ps.setString(4, campanha.getCategoria().trim());
            ps.setString(5, campanha.getLocal().trim());

            if (imagem != null) {
                ps.setBinaryStream(
                        6,
                        new ByteArrayInputStream(imagem),
                        imagem.length
                );
            } else {
                ps.setNull(6, java.sql.Types.BLOB);
            }

            return ps;
        });
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
            U.NOME AS NOME_ONG,
            CASE
                WHEN C.IMAGEM IS NOT NULL THEN 1
                ELSE 0
            END AS TEM_IMAGEM,
            CASE
                WHEN U.FOTO_PERFIL IS NOT NULL THEN 1
                ELSE 0
            END AS TEM_FOTO_ONG
        FROM CAMPANHAS C
        INNER JOIN ONGS O ON O.ID = C.ONG_ID
        INNER JOIN USUARIOS U ON U.ID = O.USUARIO_ID
        WHERE C.STATUS = 'ATIVA'
        ORDER BY C.DATA_CRIACAO DESC
        """;

        return jdbc.queryForList(sql);
    }

    public byte[] buscarImagemCampanha(Long id) {
        List<byte[]> imagens = jdbc.query(
                "SELECT IMAGEM FROM CAMPANHAS WHERE ID = ?",
                (rs, rowNum) -> {
                    Blob blob = rs.getBlob("IMAGEM");

                    if (blob == null) {
                        return null;
                    }

                    try {
                        return blob.getBytes(1, (int) blob.length());
                    } finally {
                        blob.free();
                    }
                },
                id
        );

        return imagens.isEmpty() ? null : imagens.get(0);
    }

    public byte[] buscarFotoOng(Long ongId) {
        List<byte[]> fotos = jdbc.query(
                """
                SELECT U.FOTO_PERFIL
                FROM ONGS O
                INNER JOIN USUARIOS U ON U.ID = O.USUARIO_ID
                WHERE O.ID = ?
                """,
                (rs, rowNum) -> {
                    Blob blob = rs.getBlob("FOTO_PERFIL");

                    if (blob == null) {
                        return null;
                    }

                    try {
                        return blob.getBytes(1, (int) blob.length());
                    } finally {
                        blob.free();
                    }
                },
                ongId
        );

        return fotos.isEmpty() ? null : fotos.get(0);
    }

    // Busca uma campanha somente se pertencer à ONG informada
    public Optional<Campanha> buscarPorIdEOng(Long campanhaId, Long ongId) {
        String sql = """
        SELECT *
        FROM CAMPANHAS
        WHERE ID = ?
          AND ONG_ID = ?
        """;

        List<Campanha> resultado = jdbc.query(
                sql,
                rowMapper,
                campanhaId,
                ongId
        );

        return resultado.stream().findFirst();
    }

    // Atualiza os dados da campanha
// Se não houver nova imagem, preserva a imagem anterior
    public int atualizar(
            Campanha campanha,
            Long campanhaId,
            Long ongId,
            byte[] imagem) {

        if (imagem == null) {
            String sql = """
            UPDATE CAMPANHAS
            SET TITULO = ?,
                DESCRICAO = ?,
                CATEGORIA = ?,
                LOCAL = ?
            WHERE ID = ?
              AND ONG_ID = ?
            """;

            return jdbc.update(
                    sql,
                    campanha.getTitulo().trim(),
                    campanha.getDescricao().trim(),
                    campanha.getCategoria().trim(),
                    campanha.getLocal().trim(),
                    campanhaId,
                    ongId
            );
        }

        String sql = """
        UPDATE CAMPANHAS
        SET TITULO = ?,
            DESCRICAO = ?,
            CATEGORIA = ?,
            LOCAL = ?,
            IMAGEM = ?
        WHERE ID = ?
          AND ONG_ID = ?
        """;

        return jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setString(1, campanha.getTitulo().trim());
            ps.setString(2, campanha.getDescricao().trim());
            ps.setString(3, campanha.getCategoria().trim());
            ps.setString(4, campanha.getLocal().trim());

            ps.setBinaryStream(
                    5,
                    new ByteArrayInputStream(imagem),
                    imagem.length
            );

            ps.setLong(6, campanhaId);
            ps.setLong(7, ongId);

            return ps;
        });
    }

    // Exclui somente se a campanha pertencer à ONG informada
    public int excluir(Long campanhaId, Long ongId) {
        String sql = """
        DELETE FROM CAMPANHAS
        WHERE ID = ?
          AND ONG_ID = ?
        """;

        return jdbc.update(sql, campanhaId, ongId);
    }

}
