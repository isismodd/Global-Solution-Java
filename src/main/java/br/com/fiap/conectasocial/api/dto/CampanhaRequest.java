package br.com.fiap.conectasocial.api.dto;

import br.com.fiap.conectasocial.api.entity.StatusCampanha;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CampanhaRequest(

        @NotBlank(message = "O título é obrigatório")
        @Size(max = 150, message = "O título deve ter no máximo 150 caracteres")
        @Schema(example = "Arrecadação de alimentos")
        String titulo,

        @NotBlank(message = "A descrição é obrigatória")
        @Size(max = 2000, message = "A descrição deve ter no máximo 2000 caracteres")
        @Schema(example = "Estamos arrecadando cestas básicas para famílias da zona leste.")
        String descricao,

        @NotBlank(message = "A categoria é obrigatória")
        @Size(max = 50, message = "A categoria deve ter no máximo 50 caracteres")
        @Schema(example = "Alimentação")
        String categoria,

        @NotBlank(message = "O local é obrigatório")
        @Size(max = 150, message = "O local deve ter no máximo 150 caracteres")
        @Schema(example = "São Paulo - SP")
        String local,

        @Schema(description = "Opcional. Usado na edição para ativar/inativar a campanha.",
                example = "ATIVA")
        StatusCampanha status
) {
}
