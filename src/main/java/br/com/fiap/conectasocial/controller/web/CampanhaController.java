
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
        model.addAttribute("campanha", new Campanha());

        return "ong/campanha-form";
    }

    @PostMapping("/ong/campanhas")
    public String cadastrar(
            @ModelAttribute Campanha campanha,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            Usuario usuario = (Usuario) authentication.getPrincipal();

            campanhaService.cadastrar(campanha, usuario.getId());

            redirectAttributes.addFlashAttribute(
                    "sucesso", "Campanha cadastrada com sucesso!");

            return "redirect:/ong/campanhas";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
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
}
