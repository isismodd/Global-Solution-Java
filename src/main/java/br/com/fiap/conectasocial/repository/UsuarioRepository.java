package br.com.fiap.conectasocial.repository;

import br.com.fiap.conectasocial.model.Usuario;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class UsuarioRepository {
    private final JdbcTemplate jdbc;
    public UsuarioRepository(JdbcTemplate jdbc){this.jdbc=jdbc;}

    public Optional<Usuario> buscarPorEmail(String email){
        List<Usuario> encontrados=jdbc.query("SELECT ID,NOME,EMAIL,SENHA,TELEFONE,PERFIL,ATIVO,DATA_CADASTRO FROM USUARIOS WHERE LOWER(EMAIL)=LOWER(?)", this::mapear, email.trim());
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
    private Usuario mapear(java.sql.ResultSet rs,int row) throws java.sql.SQLException {
        var ts=rs.getTimestamp("DATA_CADASTRO");
        return new Usuario(rs.getLong("ID"),rs.getString("NOME"),rs.getString("EMAIL"),rs.getString("SENHA"),rs.getString("TELEFONE"),rs.getString("PERFIL"),rs.getInt("ATIVO")==1,ts==null?null:ts.toLocalDateTime());
    }
}
