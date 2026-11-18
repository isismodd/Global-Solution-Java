package br.com.fiap.conectasocial.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

/** Formato padrão de erro de TODA a API. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErroResponse(
        LocalDateTime timestamp,
        int status,
        String erro,
        String mensagem,
        String caminho,
        List<CampoErro> campos
) {
    public record CampoErro(String campo, String mensagem) {
    }

    public static ErroResponse de(HttpStatus status, String mensagem, String caminho) {
        return new ErroResponse(LocalDateTime.now(), status.value(),
                status.getReasonPhrase(), mensagem, caminho, null);
    }

    public static ErroResponse de(HttpStatus status, String mensagem, String caminho,
                                  List<CampoErro> campos) {
        return new ErroResponse(LocalDateTime.now(), status.value(),
                status.getReasonPhrase(), mensagem, caminho, campos);
    }
}
