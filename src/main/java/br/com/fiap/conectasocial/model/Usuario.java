package br.com.fiap.conectasocial.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public class Usuario implements UserDetails {
    private Long id;
    private String nome;
    private String email;
    private String senha;
    private String telefone;
    private String perfil;
    private Boolean ativo;
    private LocalDateTime dataCadastro;
    private byte[] fotoPerfil;

    public Usuario() {}
    public Usuario(Long id, String nome, String email, String senha, String telefone,
                   String perfil, Boolean ativo, LocalDateTime dataCadastro) {
        this.id=id; this.nome=nome; this.email=email; this.senha=senha;
        this.telefone=telefone; this.perfil=perfil; this.ativo=ativo; this.dataCadastro=dataCadastro;
    }
    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + perfil));
    }
    @Override public String getPassword() { return senha; }
    @Override public String getUsername() { return email; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return Boolean.TRUE.equals(ativo); }

    public byte[] getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(byte[] fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public String getNome(){return nome;} public void setNome(String v){nome=v;}
    public String getEmail(){return email;} public void setEmail(String v){email=v;}
    public String getSenha(){return senha;} public void setSenha(String v){senha=v;}
    public String getTelefone(){return telefone;} public void setTelefone(String v){telefone=v;}
    public String getPerfil(){return perfil;} public void setPerfil(String v){perfil=v;}
    public Boolean getAtivo(){return ativo;} public void setAtivo(Boolean v){ativo=v;}
    public LocalDateTime getDataCadastro(){return dataCadastro;} public void setDataCadastro(LocalDateTime v){dataCadastro=v;}
}
