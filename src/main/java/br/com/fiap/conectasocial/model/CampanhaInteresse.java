package br.com.fiap.conectasocial.model;

import java.time.LocalDateTime;

public class CampanhaInteresse {

    private Long id;
    private Long campanhaId;
    private Long usuarioId;
    private String tipoInteresse;
    private LocalDateTime dataInteresse;

    private String nomeUsuario;
    private String emailUsuario;
    private String tituloCampanha;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCampanhaId() {
        return campanhaId;
    }

    public void setCampanhaId(Long campanhaId) {
        this.campanhaId = campanhaId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getTipoInteresse() {
        return tipoInteresse;
    }

    public void setTipoInteresse(String tipoInteresse) {
        this.tipoInteresse = tipoInteresse;
    }

    public LocalDateTime getDataInteresse() {
        return dataInteresse;
    }

    public void setDataInteresse(LocalDateTime dataInteresse) {
        this.dataInteresse = dataInteresse;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }

    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }

    public String getTituloCampanha() {
        return tituloCampanha;
    }

    public void setTituloCampanha(String tituloCampanha) {
        this.tituloCampanha = tituloCampanha;
    }
}