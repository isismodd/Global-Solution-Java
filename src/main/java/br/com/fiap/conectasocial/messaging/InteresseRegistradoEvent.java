package br.com.fiap.conectasocial.messaging;

import br.com.fiap.conectasocial.api.entity.TipoInteresse;

import java.time.LocalDateTime;

/**
 * Evento publicado quando um voluntário ou beneficiário demonstra interesse
 * em uma campanha. Carrega tudo que o consumidor precisa (não consulta o banco).
 */
public record InteresseRegistradoEvent(
        Long interesseId,
        Long campanhaId,
        String tituloCampanha,
        String nomeOng,
        String emailOng,
        Long usuarioId,
        String nomeUsuario,
        TipoInteresse tipoInteresse,
        LocalDateTime dataInteresse
) {
}
