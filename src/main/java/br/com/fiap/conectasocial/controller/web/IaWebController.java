package br.com.fiap.conectasocial.controller.web;

import br.com.fiap.conectasocial.api.dto.RecomendacaoRequest;
import br.com.fiap.conectasocial.api.dto.RecomendacaoResponse;
import br.com.fiap.conectasocial.api.service.RecomendacaoIaService;
import br.com.fiap.conectasocial.model.Usuario;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint de recomendação para a aplicação web.
 * Utiliza a sessão autenticada pelo formLogin do Spring Security.
 * O endpoint /api/ia permanece separado e continua usando JWT.
 */
@RestController
@RequestMapping("/ia")
public class IaWebController {

    private final RecomendacaoIaService service;

    public IaWebController(RecomendacaoIaService service) {
        this.service = service;
    }

    @PostMapping("/recomendar-campanhas")
    public RecomendacaoResponse recomendar(
            @Valid @RequestBody RecomendacaoRequest req,
            @AuthenticationPrincipal Usuario usuario) {
        return service.recomendar(req.texto(), usuario.getPerfil());
    }
}
