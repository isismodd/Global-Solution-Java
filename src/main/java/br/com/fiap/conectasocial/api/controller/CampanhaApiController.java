package br.com.fiap.conectasocial.api.controller;

import br.com.fiap.conectasocial.api.dto.CampanhaRequest;
import br.com.fiap.conectasocial.api.dto.CampanhaResponse;
import br.com.fiap.conectasocial.api.dto.PageResponse;
import br.com.fiap.conectasocial.api.service.CampanhaApiService;
import br.com.fiap.conectasocial.model.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/campanhas")
@Tag(name = "Campanhas", description = "CRUD de campanhas das ONGs")
@SecurityRequirement(name = "bearerAuth")
public class CampanhaApiController {

    private final CampanhaApiService service;

    public CampanhaApiController(CampanhaApiService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista campanhas ativas (paginado), com filtro opcional por categoria")
    public PageResponse<CampanhaResponse> listar(
            @RequestParam(required = false) String categoria,
            @ParameterObject @PageableDefault(size = 10, sort = "dataCriacao",
                    direction = Sort.Direction.DESC) Pageable pageable) {
        return service.listarAtivas(categoria, pageable);
    }

    @GetMapping("/minhas")
    @Operation(summary = "Lista as campanhas da ONG logada (papel ONG)")
    public PageResponse<CampanhaResponse> minhas(
            @AuthenticationPrincipal Usuario usuario,
            @ParameterObject @PageableDefault(size = 10, sort = "dataCriacao",
                    direction = Sort.Direction.DESC) Pageable pageable) {
        return service.listarDaOng(usuario.getId(), pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma campanha pelo id")
    public CampanhaResponse buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    @Operation(summary = "Cria uma campanha (papel ONG)")
    public ResponseEntity<CampanhaResponse> criar(@Valid @RequestBody CampanhaRequest req,
                                                  @AuthenticationPrincipal Usuario usuario) {
        CampanhaResponse criada = service.criar(req, usuario.getId());
        return ResponseEntity.created(URI.create("/api/campanhas/" + criada.id())).body(criada);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma campanha da própria ONG (papel ONG)")
    public CampanhaResponse atualizar(@PathVariable Long id,
                                      @Valid @RequestBody CampanhaRequest req,
                                      @AuthenticationPrincipal Usuario usuario) {
        return service.atualizar(id, req, usuario.getId());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui uma campanha da própria ONG (papel ONG)")
    public ResponseEntity<Void> excluir(@PathVariable Long id,
                                        @AuthenticationPrincipal Usuario usuario) {
        service.excluir(id, usuario.getId());
        return ResponseEntity.noContent().build();
    }
}
