package br.com.fiap.conectasocial.api.dto;

import java.util.List;

public record RecomendacaoResponse(
        String resumo,
        List<CampanhaRecomendada> recomendacoes
) {
    public record CampanhaRecomendada(CampanhaResponse campanha, String motivo) {
    }
}
