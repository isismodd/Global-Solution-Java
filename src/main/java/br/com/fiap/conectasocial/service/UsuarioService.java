package br.com.fiap.conectasocial.service;

import br.com.fiap.conectasocial.dto.auth.CadastroRequest;
import br.com.fiap.conectasocial.model.Usuario;
import br.com.fiap.conectasocial.repository.UsuarioRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;

@Service
public class UsuarioService {
    private static final Set<String> PERFIS=Set.of("VOLUNTARIO","BENEFICIARIO","ONG");
    private static final String VERSAO_POLITICA="1.0";
    private final UsuarioRepository usuarios;
    private final PasswordEncoder passwordEncoder;
    private final org.springframework.jdbc.core.JdbcTemplate jdbc;

    public UsuarioService(UsuarioRepository usuarios, PasswordEncoder passwordEncoder, org.springframework.jdbc.core.JdbcTemplate jdbc){
        this.usuarios=usuarios; this.passwordEncoder=passwordEncoder; this.jdbc=jdbc;
    }

    @Transactional
    public void cadastrar(CadastroRequest r){
        String perfil=r.getPerfil()==null?"":r.getPerfil().trim().toUpperCase();
        if(!PERFIS.contains(perfil)) throw new IllegalArgumentException("Selecione um tipo de cadastro válido.");
        if(!r.isAceitouPolitica()) throw new IllegalArgumentException("É necessário aceitar a política de uso.");
        if(!r.getSenha().equals(r.getConfirmarSenha())) throw new IllegalArgumentException("As senhas não coincidem.");
        if(usuarios.buscarPorEmail(r.getEmail()).isPresent()) throw new IllegalArgumentException("Este e-mail já está cadastrado.");
        validarPerfil(r,perfil);
        Usuario u=new Usuario(); u.setNome(r.getNome().trim()); u.setEmail(r.getEmail().trim().toLowerCase());
        u.setSenha(passwordEncoder.encode(r.getSenha())); u.setTelefone(r.getTelefone()); u.setPerfil(perfil); u.setAtivo(true);
        try {
            long id=usuarios.inserir(u);
            switch(perfil){
                case "VOLUNTARIO" -> jdbc.update("INSERT INTO VOLUNTARIOS (USUARIO_ID,CPF,DATA_NASCIMENTO,ESTADO,CIDADE,BAIRRO) VALUES (?,?,?,?,?,?)",id,digitos(r.getCpf()),java.sql.Date.valueOf(LocalDate.parse(r.getDataNascimento())),r.getEstado(),r.getCidade(),r.getBairro());
                case "BENEFICIARIO" -> jdbc.update("INSERT INTO BENEFICIARIOS (USUARIO_ID,CPF,DATA_NASCIMENTO,ESTADO,CIDADE,BAIRRO) VALUES (?,?,?,?,?,?)",id,digitos(r.getCpf()),java.sql.Date.valueOf(LocalDate.parse(r.getDataNascimento())),r.getEstado(),r.getCidade(),r.getBairro());
                case "ONG" -> jdbc.update("INSERT INTO ONGS (USUARIO_ID,CNPJ,NOME_RESPONSAVEL,ESTADO,CIDADE,BAIRRO,DESCRICAO) VALUES (?,?,?,?,?,?,?)",id,digitos(r.getCnpj()),r.getNomeResponsavel(),r.getEstado(),r.getCidade(),r.getBairro(),r.getDescricao());
            }
            jdbc.update("INSERT INTO ACEITES_POLITICA (USUARIO_ID,VERSAO_POLITICA,DATA_ACEITE) VALUES (?,?,SYSTIMESTAMP)",id,VERSAO_POLITICA);
        } catch (DuplicateKeyException ex){ throw new IllegalArgumentException("E-mail ou documento já cadastrado."); }
    }
    private void validarPerfil(CadastroRequest r,String perfil){
        if("ONG".equals(perfil)){
            if(vazio(r.getCnpj())||vazio(r.getNomeResponsavel())||vazio(r.getEstado())||vazio(r.getCidade())) throw new IllegalArgumentException("Preencha CNPJ, responsável e região da ONG.");
            if(digitos(r.getCnpj()).length()!=14) throw new IllegalArgumentException("Informe um CNPJ válido com 14 dígitos.");
        } else {
            if(vazio(r.getCpf())||vazio(r.getDataNascimento())||vazio(r.getEstado())||vazio(r.getCidade())) throw new IllegalArgumentException("Preencha CPF, data de nascimento e região.");
            if(digitos(r.getCpf()).length()!=11) throw new IllegalArgumentException("Informe um CPF válido com 11 dígitos.");
            try { if(LocalDate.parse(r.getDataNascimento()).isAfter(LocalDate.now())) throw new IllegalArgumentException("A data de nascimento não pode ser futura."); }
            catch(java.time.format.DateTimeParseException e){throw new IllegalArgumentException("Informe uma data de nascimento válida.");}
        }
    }
    private boolean vazio(String s){return s==null||s.isBlank();}
    private String digitos(String s){return s==null?"":s.replaceAll("\\D","");}
}
