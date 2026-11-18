package br.com.fiap.conectasocial.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;

/** Mapeia a tabela ONGS (somente leitura na API). */
@Entity
@Immutable
@Table(name = "ONGS")
public class OngEntity {

    @Id
    @Column(name = "ID")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USUARIO_ID", nullable = false)
    private UsuarioEntity usuario;

    @Column(name = "CNPJ")
    private String cnpj;

    @Column(name = "CIDADE")
    private String cidade;

    @Column(name = "ESTADO")
    private String estado;

    protected OngEntity() {
    }

    public Long getId() { return id; }
    public UsuarioEntity getUsuario() { return usuario; }
    public String getCnpj() { return cnpj; }
    public String getCidade() { return cidade; }
    public String getEstado() { return estado; }
}
