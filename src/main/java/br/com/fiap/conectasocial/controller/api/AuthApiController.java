package br.com.fiap.conectasocial.controller.api;

import br.com.fiap.conectasocial.api.dto.MensagemResponse;
import br.com.fiap.conectasocial.config.JwtService;
import br.com.fiap.conectasocial.dto.auth.CadastroRequest;
import br.com.fiap.conectasocial.dto.auth.LoginRequest;
import br.com.fiap.conectasocial.dto.auth.LoginResponse;
import br.com.fiap.conectasocial.dto.auth.UserResponse;
import br.com.fiap.conectasocial.model.Usuario;
import br.com.fiap.conectasocial.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "Cadastro e login (gera o token JWT)")
public class AuthApiController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioService usuarioService;

    public AuthApiController(AuthenticationManager authenticationManager,
                             JwtService jwtService,
                             UsuarioService usuarioService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/register")
    @Operation(summary = "Cadastra ONG, voluntário ou beneficiário")
    public ResponseEntity<MensagemResponse> registrar(@Valid @RequestBody CadastroRequest request) {
        usuarioService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MensagemResponse("Cadastro realizado com sucesso."));
    }

    @PostMapping("/login")
    @Operation(summary = "Autentica e devolve o token JWT")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha()));
        Usuario usuario = (Usuario) authentication.getPrincipal();
        String token = jwtService.gerarToken(usuario);
        return new LoginResponse(token,
                new UserResponse(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getPerfil()));
    }
}
