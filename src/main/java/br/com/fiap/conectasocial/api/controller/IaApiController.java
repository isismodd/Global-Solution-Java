package br.com.fiap.conectasocial.api.controller;

import br.com.fiap.conectasocial.api.dto.RecomendacaoRequest;
import br.com.fiap.conectasocial.api.dto.RecomendacaoResponse;
import br.com.fiap.conectasocial.api.service.RecomendacaoIaService;
import br.com.fiap.conectasocial.model.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ia")
@Tag(name = "IA", description = "Recursos com IA generativa (Spring AI + Gemini)")
@SecurityRequirement(name = "bearerAuth")
public class IaApiController {

    private final RecomendacaoIaService service;

    public IaApiController(RecomendacaoIaService service) {
        this.service = service;
    }

    @PostMapping("/recomendar-campanhas")
    @Operation(summary = "Recomenda campanhas ativas a partir de um texto livre (VOLUNTARIO e BENEFICIARIO)",
            description = "A IA analisa o texto do usuário e escolhe, entre as campanhas ativas reais, "
                    + "as mais compatíveis, explicando o motivo de cada uma.")
    public RecomendacaoResponse recomendar(@Valid @RequestBody RecomendacaoRequest req,
                                           @AuthenticationPrincipal Usuario usuario) {
        return service.recomendar(req.texto(), usuario.getPerfil());
    }
}
