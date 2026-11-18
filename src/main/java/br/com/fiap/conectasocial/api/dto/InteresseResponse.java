package br.com.fiap.conectasocial.api.dto;

import br.com.fiap.conectasocial.api.entity.InteresseCampanhaEntity;
import br.com.fiap.conectasocial.api.entity.TipoInteresse;

import java.time.LocalDateTime;

public record InteresseResponse(
        Long id,
        Long campanhaId,
        String tituloCampanha,
        Long usuarioId,
        String nomeUsuario,
        String emailUsuario,
        TipoInteresse tipoInteresse,
        LocalDateTime dataInteresse
) {
    public static InteresseResponse de(InteresseCampanhaEntity i) {
        return new InteresseResponse(
                i.getId(),
                i.getCampanha().getId(),
                i.getCampanha().getTitulo(),
                i.getUsuario().getId(),
                i.getUsuario().getNome(),
                i.getUsuario().getEmail(),
                i.getTipoInteresse(),
                i.getDataInteresse()
        );
    }
}
