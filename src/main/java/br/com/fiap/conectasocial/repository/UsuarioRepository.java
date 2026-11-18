package br.com.fiap.conectasocial.repository;

import br.com.fiap.conectasocial.model.Usuario;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import java.io.ByteArrayInputStream;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class UsuarioRepository {
    private final JdbcTemplate jdbc;
    public UsuarioRepository(JdbcTemplate jdbc){this.jdbc=jdbc;}


    public Optional<Usuario> buscarPorEmail(String email){
        List<Usuario> encontrados = jdbc.query(
                "SELECT ID, NOME, EMAIL, SENHA, TELEFONE, PERFIL, ATIVO, DATA_CADASTRO, FOTO_PERFIL " +
                        "FROM USUARIOS WHERE LOWER(EMAIL) = LOWER(?)",
                this::mapear,
                email.trim()
        );
        return encontrados.stream().findFirst();
    }

    public long inserir(Usuario u){
        KeyHolder key=new GeneratedKeyHolder();
        jdbc.update(conn->{
            PreparedStatement ps=conn.prepareStatement("INSERT INTO USUARIOS (NOME,EMAIL,SENHA,TELEFONE,PERFIL,ATIVO,DATA_CADASTRO) VALUES (?,?,?,?,?,1,SYSDATE)", new String[]{"ID"});
            ps.setString(1,u.getNome()); ps.setString(2,u.getEmail().trim().toLowerCase()); ps.setString(3,u.getSenha()); ps.setString(4,u.getTelefone()); ps.setString(5,u.getPerfil()); return ps;
        },key);
        Number id=key.getKey();
        if(id==null) throw new IllegalStateException("Não foi possível obter o ID do usuário criado.");
        return id.longValue();
    }
    private Usuario mapear(java.sql.ResultSet rs, int row)
            throws java.sql.SQLException {

        var ts = rs.getTimestamp("DATA_CADASTRO");

        Usuario usuario = new Usuario(
                rs.getLong("ID"),
                rs.getString("NOME"),
                rs.getString("EMAIL"),
                rs.getString("SENHA"),
                rs.getString("TELEFONE"),
                rs.getString("PERFIL"),
                rs.getInt("ATIVO") == 1,
                ts == null ? null : ts.toLocalDateTime()
        );

        java.sql.Blob blob = rs.getBlob("FOTO_PERFIL");

        if (blob != null) {
            usuario.setFotoPerfil(blob.getBytes(1, (int) blob.length()));
            blob.free();
        }

        return usuario;
    }


    public Optional<Usuario> buscarPorId(Long id) {
        List<Usuario> encontrados = jdbc.query(
                "SELECT ID, NOME, EMAIL, SENHA, TELEFONE, PERFIL, ATIVO, DATA_CADASTRO, FOTO_PERFIL " +
                        "FROM USUARIOS WHERE ID = ?",
                this::mapear,
                id
        );

        return encontrados.stream().findFirst();
    }

    public boolean emailJaCadastrado(String email, Long idUsuario) {
        Integer quantidade = jdbc.queryForObject(
                "SELECT COUNT(*) FROM USUARIOS " +
                        "WHERE LOWER(EMAIL) = LOWER(?) AND ID <> ?",
                Integer.class,
                email.trim(),
                idUsuario
        );

        return quantidade != null && quantidade > 0;
    }

    public void atualizarDadosPerfil(
            Long id,
            String nome,
            String email,
            String telefone) {

        jdbc.update(
                "UPDATE USUARIOS SET NOME = ?, EMAIL = ?, TELEFONE = ? WHERE ID = ?",
                nome.trim(),
                email.trim().toLowerCase(),
                telefone,
                id
        );
    }

    public void atualizarFotoPerfil(Long id, byte[] foto) {
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "UPDATE USUARIOS SET FOTO_PERFIL = ? WHERE ID = ?"
            );

            ps.setBinaryStream(
                    1,
                    new ByteArrayInputStream(foto),
                    foto.length
            );

            ps.setLong(2, id);

            return ps;
        });
    }

}
