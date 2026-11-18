
package br.com.fiap.conectasocial.controller.web;

import br.com.fiap.conectasocial.model.Usuario;
import br.com.fiap.conectasocial.service.PerfilService;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

import java.io.IOException;

@Controller
public class PerfilController {

    private final PerfilService perfilService;

    public PerfilController(PerfilService perfilService) {
        this.perfilService = perfilService;
    }

    // Abre o perfil do usuário autenticado
    @GetMapping("/perfil")
    public String perfil(Authentication authentication, Model model) {

        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();

        Usuario usuario = perfilService.buscarPorId(usuarioLogado.getId());

        model.addAttribute("usuario", usuario);

        return "perfil";
    }

    // Atualiza nome, e-mail e telefone
    @PostMapping("/perfil/atualizar")
    public String atualizarDados(
            @RequestParam String nome,
            @RequestParam String email,
            @RequestParam(required = false) String telefone,
            Authentication authentication,
            HttpServletRequest request,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes) {

        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();

        try {
            perfilService.atualizarDados(
                    usuarioLogado.getId(),
                    nome,
                    email,
                    telefone
            );

            atualizarSessao(usuarioLogado.getId(), request, response);

            redirectAttributes.addFlashAttribute(
                    "sucesso",
                    "Seus dados foram atualizados com sucesso!"
            );

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "erro",
                    e.getMessage()
            );
        }

        return "redirect:/perfil";
    }

    // Atualiza a foto de perfil
    @PostMapping("/perfil/foto")
    public String atualizarFoto(
            @RequestParam("foto") MultipartFile foto,
            Authentication authentication,
            HttpServletRequest request,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes) {

        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();

        try {
            perfilService.atualizarFoto(usuarioLogado.getId(), foto);

            atualizarSessao(usuarioLogado.getId(), request, response);

            redirectAttributes.addFlashAttribute(
                    "sucesso",
                    "Sua foto de perfil foi atualizada!"
            );

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "erro",
                    e.getMessage()
            );

        } catch (IOException e) {

            redirectAttributes.addFlashAttribute(
                    "erro",
                    "Não foi possível processar a imagem."
            );
        }

        return "redirect:/perfil";
    }

    // Exibe a foto armazenada no Oracle
    @GetMapping("/perfil/foto")
    public ResponseEntity<byte[]> exibirFoto(Authentication authentication) {

        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();

        Usuario usuario = perfilService.buscarPorId(usuarioLogado.getId());

        byte[] foto = usuario.getFotoPerfil();

        if (foto == null || foto.length == 0) {
            return ResponseEntity.notFound().build();
        }

        MediaType tipo;

        if (foto.length >= 3 &&
                (foto[0] & 0xFF) == 0xFF &&
                (foto[1] & 0xFF) == 0xD8 &&
                (foto[2] & 0xFF) == 0xFF) {

            tipo = MediaType.IMAGE_JPEG;

        } else if (foto.length >= 8 &&
                (foto[0] & 0xFF) == 0x89 &&
                foto[1] == 0x50 &&
                foto[2] == 0x4E &&
                foto[3] == 0x47) {

            tipo = MediaType.IMAGE_PNG;

        } else if (foto.length >= 12 &&
                foto[0] == 'R' &&
                foto[1] == 'I' &&
                foto[2] == 'F' &&
                foto[3] == 'F' &&
                foto[8] == 'W' &&
                foto[9] == 'E' &&
                foto[10] == 'B' &&
                foto[11] == 'P') {

            tipo = MediaType.parseMediaType("image/webp");

        } else {

            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build();
        }

        return ResponseEntity.ok()
                .contentType(tipo)
                .cacheControl(CacheControl.noCache())
                .body(foto);
    }

    // Atualiza os dados do usuário na sessão autenticada
    private void atualizarSessao(
            Long id,
            HttpServletRequest request,
            HttpServletResponse response) {

        Usuario usuarioAtualizado = perfilService.buscarPorId(id);

        UsernamePasswordAuthenticationToken novaAutenticacao =
                new UsernamePasswordAuthenticationToken(
                        usuarioAtualizado,
                        null,
                        usuarioAtualizado.getAuthorities()
                );

        SecurityContext contexto = SecurityContextHolder.createEmptyContext();

        contexto.setAuthentication(novaAutenticacao);

        SecurityContextHolder.setContext(contexto);

        SecurityContextRepository repositorio =
                new HttpSessionSecurityContextRepository();

        repositorio.saveContext(contexto, request, response);
    }
}
