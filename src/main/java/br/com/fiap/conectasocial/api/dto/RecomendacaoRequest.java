package br.com.fiap.conectasocial.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RecomendacaoRequest(

        @NotBlank(message = "Descreva o que você precisa ou quer fazer")
        @Size(min = 10, max = 1000, message = "O texto deve ter entre 10 e 1000 caracteres")
        @Schema(example = "Sou estudante, tenho os sábados livres e quero ajudar com alimentação.")
        String texto
) {
}
