
package br.com.fiap.conectasocial.controller.web;

import br.com.fiap.conectasocial.model.Campanha;
import br.com.fiap.conectasocial.model.Usuario;
import br.com.fiap.conectasocial.service.CampanhaService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import java.nio.charset.StandardCharsets;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class CampanhaController {

    private final CampanhaService campanhaService;

    public CampanhaController(CampanhaService campanhaService) {
        this.campanhaService = campanhaService;
    }

    private void adicionarDadosUsuario(Authentication authentication, Model model) {
        Usuario usuario = (Usuario) authentication.getPrincipal();

        model.addAttribute("usuario", usuario);
        model.addAttribute("perfil", usuario.getPerfil());
        model.addAttribute("nomeExibicao", usuario.getNome());
    }

    @GetMapping("/ong/campanhas/nova")
    public String novaCampanha(Authentication authentication, Model model) {
        adicionarDadosUsuario(authentication, model);
        model.addAttribute("modoEdicao", false);
        model.addAttribute("campanha", new Campanha());

        return "ong/campanha-form";
    }

    @PostMapping("/ong/campanhas")
    public String cadastrar(
            @ModelAttribute Campanha campanha,
            @RequestParam(value = "arquivoImagem", required = false) MultipartFile imagem,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            Usuario usuario = (Usuario) authentication.getPrincipal();

            campanhaService.cadastrar(
                    campanha,
                    usuario.getId(),
                    imagem
            );

            redirectAttributes.addFlashAttribute(
                    "sucesso", "Campanha cadastrada com sucesso!");

            return "redirect:/ong/campanhas";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:/ong/campanhas/nova";

        } catch (IOException e) {
            redirectAttributes.addFlashAttribute(
                    "erro", "Não foi possível processar a imagem da campanha.");
            return "redirect:/ong/campanhas/nova";
        }
    }

    @GetMapping("/ong/campanhas")
    public String minhasCampanhas(
            Authentication authentication,
            Model model) {

        adicionarDadosUsuario(authentication, model);

        Usuario usuario = (Usuario) authentication.getPrincipal();

        model.addAttribute(
                "campanhas",
                campanhaService.listarPorOng(usuario.getId()));

        return "ong/minhas-campanhas";
    }

    @GetMapping("/campanhas/{id}/imagem")
    public ResponseEntity<byte[]> imagemCampanha(@PathVariable Long id) {
        return respostaImagem(campanhaService.buscarImagemCampanha(id));
    }

    @GetMapping("/ongs/{ongId}/foto")
    public ResponseEntity<byte[]> fotoOng(@PathVariable Long ongId) {
        return respostaImagem(campanhaService.buscarFotoOng(ongId));
    }

    private ResponseEntity<byte[]> respostaImagem(byte[] imagem) {
        if (imagem == null || imagem.length == 0) {
            return ResponseEntity.notFound().build();
        }

        MediaType tipo;

        if (imagem.length >= 3
                && (imagem[0] & 0xFF) == 0xFF
                && (imagem[1] & 0xFF) == 0xD8
                && (imagem[2] & 0xFF) == 0xFF) {
            tipo = MediaType.IMAGE_JPEG;

        } else if (imagem.length >= 8
                && (imagem[0] & 0xFF) == 0x89
                && imagem[1] == 0x50
                && imagem[2] == 0x4E
                && imagem[3] == 0x47
                && imagem[4] == 0x0D
                && imagem[5] == 0x0A
                && imagem[6] == 0x1A
                && imagem[7] == 0x0A) {
            tipo = MediaType.IMAGE_PNG;

        } else if (imagem.length >= 12
                && new String(imagem, 0, 4, StandardCharsets.US_ASCII).equals("RIFF")
                && new String(imagem, 8, 4, StandardCharsets.US_ASCII).equals("WEBP")) {
            tipo = MediaType.parseMediaType("image/webp");

        } else {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok()
                .contentType(tipo)
                .cacheControl(CacheControl.noCache())
                .body(imagem);
    }

// Abre o formulário de edição
    @GetMapping("/ong/campanhas/{id}/editar")
    public String editarCampanha(
            @PathVariable Long id,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            Usuario usuario = (Usuario) authentication.getPrincipal();

            adicionarDadosUsuario(authentication, model);

            Campanha campanha = campanhaService.buscarParaEdicao(
                    id,
                    usuario.getId()
            );
            model.addAttribute("modoEdicao", true);
            model.addAttribute("campanha", campanha);
            return "ong/campanha-form";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:/ong/campanhas";
        }
    }

    // Salva as alterações
    @PostMapping("/ong/campanhas/{id}/editar")
    public String atualizarCampanha(
            @PathVariable Long id,
            @ModelAttribute Campanha campanha,
            @RequestParam(value = "arquivoImagem", required = false)
            MultipartFile imagem,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            Usuario usuario = (Usuario) authentication.getPrincipal();

            campanhaService.editar(
                    id,
                    campanha,
                    usuario.getId(),
                    imagem
            );

            redirectAttributes.addFlashAttribute(
                    "sucesso",
                    "Campanha atualizada com sucesso!"
            );

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());

        } catch (IOException e) {
            redirectAttributes.addFlashAttribute(
                    "erro",
                    "Não foi possível processar a imagem da campanha."
            );
        }

        return "redirect:/ong/campanhas";
    }

    // Exclui a campanha
    @PostMapping("/ong/campanhas/{id}/excluir")
    public String excluirCampanha(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            Usuario usuario = (Usuario) authentication.getPrincipal();

            campanhaService.excluir(id, usuario.getId());

            redirectAttributes.addFlashAttribute(
                    "sucesso",
                    "Campanha excluída com sucesso!"
            );

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }

        return "redirect:/ong/campanhas";
    }
}
