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

/** Mapeia a tabela INTERESSES_CAMPANHAS. */
@Entity
@Table(name = "INTERESSES_CAMPANHAS")
public class InteresseCampanhaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CAMPANHA_ID", nullable = false)
    private CampanhaEntity campanha;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USUARIO_ID", nullable = false)
    private UsuarioEntity usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_INTERESSE", nullable = false, length = 30)
    private TipoInteresse tipoInteresse;

    @Column(name = "DATA_INTERESSE", nullable = false, updatable = false)
    private LocalDateTime dataInteresse;

    protected InteresseCampanhaEntity() {
    }

    public InteresseCampanhaEntity(CampanhaEntity campanha, UsuarioEntity usuario,
                                   TipoInteresse tipoInteresse) {
        this.campanha = campanha;
        this.usuario = usuario;
        this.tipoInteresse = tipoInteresse;
    }

    @PrePersist
    void aoCriar() {
        if (dataInteresse == null) dataInteresse = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public CampanhaEntity getCampanha() { return campanha; }
    public UsuarioEntity getUsuario() { return usuario; }
    public TipoInteresse getTipoInteresse() { return tipoInteresse; }
    public LocalDateTime getDataInteresse() { return dataInteresse; }
}
