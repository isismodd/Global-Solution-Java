package br.com.fiap.conectasocial.api.dto;

import br.com.fiap.conectasocial.api.entity.CampanhaEntity;
import br.com.fiap.conectasocial.api.entity.StatusCampanha;

import java.time.LocalDateTime;

public record CampanhaResponse(
        Long id,
        String titulo,
        String descricao,
        String categoria,
        String local,
        StatusCampanha status,
        LocalDateTime dataCriacao,
        Long ongId,
        String nomeOng
) {
    public static CampanhaResponse de(CampanhaEntity c) {
        return new CampanhaResponse(
                c.getId(),
                c.getTitulo(),
                c.getDescricao(),
                c.getCategoria(),
                c.getLocalizacao(),
                c.getStatus(),
                c.getDataCriacao(),
                c.getOng().getId(),
                c.getOng().getUsuario().getNome()
        );
    }
}
