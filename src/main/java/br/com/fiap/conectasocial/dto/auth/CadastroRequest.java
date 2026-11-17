package br.com.fiap.conectasocial.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CadastroRequest {
    @NotBlank @Size(max=120) private String nome;
    @NotBlank @Email @Size(max=150) private String email;
    @NotBlank @Size(min=8, max=72) private String senha;
    @NotBlank private String confirmarSenha;
    @NotBlank private String perfil;
    @Size(max=20) private String telefone;
    private String cpf;
    private String cnpj;
    private String dataNascimento;
    private String estado;
    private String cidade;
    private String bairro;
    private String nomeResponsavel;
    private String descricao;
    private boolean aceitouPolitica;
    public String getNome(){return nome;} public void setNome(String v){nome=v;}
    public String getEmail(){return email;} public void setEmail(String v){email=v;}
    public String getSenha(){return senha;} public void setSenha(String v){senha=v;}
    public String getConfirmarSenha(){return confirmarSenha;} public void setConfirmarSenha(String v){confirmarSenha=v;}
    public String getPerfil(){return perfil;} public void setPerfil(String v){perfil=v;}
    public String getTelefone(){return telefone;} public void setTelefone(String v){telefone=v;}
    public String getCpf(){return cpf;} public void setCpf(String v){cpf=v;}
    public String getCnpj(){return cnpj;} public void setCnpj(String v){cnpj=v;}
    public String getDataNascimento(){return dataNascimento;} public void setDataNascimento(String v){dataNascimento=v;}
    public String getEstado(){return estado;} public void setEstado(String v){estado=v;}
    public String getCidade(){return cidade;} public void setCidade(String v){cidade=v;}
    public String getBairro(){return bairro;} public void setBairro(String v){bairro=v;}
    public String getNomeResponsavel(){return nomeResponsavel;} public void setNomeResponsavel(String v){nomeResponsavel=v;}
    public String getDescricao(){return descricao;} public void setDescricao(String v){descricao=v;}
    public boolean isAceitouPolitica(){return aceitouPolitica;} public void setAceitouPolitica(boolean v){aceitouPolitica=v;}
}
