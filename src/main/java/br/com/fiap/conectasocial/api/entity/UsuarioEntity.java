package br.com.fiap.conectasocial.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;

/** Mapeia a tabela USUARIOS (somente leitura na API). */
@Entity
@Immutable
@Table(name = "USUARIOS")
public class UsuarioEntity {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "NOME")
    private String nome;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "PERFIL")
    private String perfil;

    protected UsuarioEntity() {
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getPerfil() { return perfil; }
}
