package br.com.fiap.conectasocial.api.controller;

import br.com.fiap.conectasocial.api.dto.InteresseResponse;
import br.com.fiap.conectasocial.api.dto.PageResponse;
import br.com.fiap.conectasocial.api.service.InteresseApiService;
import br.com.fiap.conectasocial.model.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Interesses", description = "Voluntários e beneficiários demonstram interesse em campanhas")
@SecurityRequirement(name = "bearerAuth")
public class InteresseApiController {

    private final InteresseApiService service;

    public InteresseApiController(InteresseApiService service) {
        this.service = service;
    }

    @PostMapping("/campanhas/{campanhaId}/interesses")
    @Operation(summary = "Registra interesse na campanha (papéis VOLUNTARIO e BENEFICIARIO)")
    public ResponseEntity<InteresseResponse> registrar(@PathVariable Long campanhaId,
                                                       @AuthenticationPrincipal Usuario usuario) {
        InteresseResponse criado = service.registrar(campanhaId, usuario.getId(), usuario.getPerfil());
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @GetMapping("/campanhas/{campanhaId}/interesses")
    @Operation(summary = "Lista os interessados de uma campanha da própria ONG (papel ONG)")
    public PageResponse<InteresseResponse> listarDaCampanha(
            @PathVariable Long campanhaId,
            @AuthenticationPrincipal Usuario usuario,
            @ParameterObject @PageableDefault(size = 10, sort = "dataInteresse",
                    direction = Sort.Direction.DESC) Pageable pageable) {
        return service.listarDaCampanha(campanhaId, usuario.getId(), pageable);
    }

    @GetMapping("/interesses/meus")
    @Operation(summary = "Lista os interesses do usuário logado (VOLUNTARIO e BENEFICIARIO)")
    public PageResponse<InteresseResponse> meus(
            @AuthenticationPrincipal Usuario usuario,
            @ParameterObject @PageableDefault(size = 10, sort = "dataInteresse",
                    direction = Sort.Direction.DESC) Pageable pageable) {
        return service.listarMeus(usuario.getId(), pageable);
    }
}
