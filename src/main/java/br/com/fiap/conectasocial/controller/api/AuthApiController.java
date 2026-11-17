package br.com.fiap.conectasocial.controller.api;

import br.com.fiap.conectasocial.config.JwtService;
import br.com.fiap.conectasocial.dto.auth.LoginRequest;
import br.com.fiap.conectasocial.dto.auth.CadastroRequest;
import br.com.fiap.conectasocial.service.UsuarioService;
import jakarta.validation.Valid;
import br.com.fiap.conectasocial.dto.auth.LoginResponse;
import br.com.fiap.conectasocial.dto.auth.UserResponse;
import br.com.fiap.conectasocial.model.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioService usuarioService;
    public AuthApiController(AuthenticationManager authenticationManager,JwtService jwtService,UsuarioService usuarioService){this.authenticationManager=authenticationManager;this.jwtService=jwtService;this.usuarioService=usuarioService;}
    @PostMapping("/register") public ResponseEntity<?> registrar(@Valid @RequestBody CadastroRequest request){
        try { usuarioService.cadastrar(request); return ResponseEntity.status(HttpStatus.CREATED).body("Cadastro realizado com sucesso."); }
        catch(IllegalArgumentException e){ return ResponseEntity.badRequest().body(e.getMessage()); }
        catch(Exception e){ return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Não foi possível concluir o cadastro."); }
    }
    @PostMapping("/login") public ResponseEntity<?> login(@RequestBody LoginRequest request){
        try {
            Authentication authentication=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(),request.getSenha()));
            Usuario usuario=(Usuario)authentication.getPrincipal();
            String token=jwtService.gerarToken(usuario);
            return ResponseEntity.ok(new LoginResponse(token,new UserResponse(usuario.getId(),usuario.getNome(),usuario.getEmail(),usuario.getPerfil())));
        } catch(Exception e){return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("E-mail ou senha inválidos.");}
    }
}
