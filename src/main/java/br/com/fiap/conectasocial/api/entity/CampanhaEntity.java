package br.com.fiap.conectasocial.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Mapeia a tabela CAMPANHAS.
 * A coluna IMAGEM (BLOB) não é mapeada de propósito: ela continua sendo
 * gerenciada pelo fluxo web existente e nunca é sobrescrita pela API.
 */
@Entity
@Table(name = "CAMPANHAS")
public class CampanhaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ONG_ID", nullable = false)
    private OngEntity ong;

    @Column(name = "TITULO", nullable = false, length = 150)
    private String titulo;

    @Column(name = "DESCRICAO", nullable = false, length = 2000)
    private String descricao;

    @Column(name = "CATEGORIA", nullable = false, length = 50)
    private String categoria;

    // "local" é palavra reservada no HQL, por isso o campo Java tem outro nome
    @Column(name = "LOCAL", nullable = false, length = 150)
    private String localizacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 20)
    private StatusCampanha status;

    @Column(name = "DATA_CRIACAO", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    protected CampanhaEntity() {
    }

    public CampanhaEntity(OngEntity ong, String titulo, String descricao,
                          String categoria, String localizacao) {
        this.ong = ong;
        this.titulo = titulo;
        this.descricao = descricao;
        this.categoria = categoria;
        this.localizacao = localizacao;
        this.status = StatusCampanha.ATIVA;
    }

    @PrePersist
    void aoCriar() {
        if (dataCriacao == null) dataCriacao = LocalDateTime.now();
        if (status == null) status = StatusCampanha.ATIVA;
    }

    public Long getId() { return id; }
    public OngEntity getOng() { return ong; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getLocalizacao() { return localizacao; }
    public void setLocalizacao(String localizacao) { this.localizacao = localizacao; }
    public StatusCampanha getStatus() { return status; }
    public void setStatus(StatusCampanha status) { this.status = status; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
}
