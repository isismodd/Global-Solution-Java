
package br.com.fiap.conectasocial.controller.web;

import br.com.fiap.conectasocial.model.Usuario;
import br.com.fiap.conectasocial.repository.CampanhaRepository;
import br.com.fiap.conectasocial.service.CampanhaInteresseService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping
public class CampanhaInteresseController {

    private final CampanhaInteresseService interesseService;
    private final CampanhaRepository campanhaRepository;

    public CampanhaInteresseController(
            CampanhaInteresseService interesseService,
            CampanhaRepository campanhaRepository) {
        this.interesseService = interesseService;
        this.campanhaRepository = campanhaRepository;
    }

    @PostMapping("/campanhas/{id}/interesse")
    public String registrarInteresse(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = (Usuario) authentication.getPrincipal();

        try {
            interesseService.registrarInteresse(
                    id,
                    usuario.getId(),
                    usuario.getPerfil()
            );

            redirectAttributes.addFlashAttribute(
                    "sucesso",
                    "Seu interesse foi registrado com sucesso!"
            );

        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute(
                    "erro",
                    "Você já demonstrou interesse nesta campanha."
            );

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(
                    "erro",
                    e.getMessage()
            );
        }

        return "redirect:/home";
    }

    @GetMapping("/ong/interesses")
    public String listarInteresses(
            Authentication authentication,
            Model model) {

        Usuario usuario = (Usuario) authentication.getPrincipal();

        Long ongId = campanhaRepository
                .buscarIdOngPorUsuario(usuario.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Organização não encontrada."
                ));

        model.addAttribute("usuario", usuario);
        model.addAttribute("perfil", usuario.getPerfil());
        model.addAttribute("nomeExibicao", usuario.getNome());

        model.addAttribute(
                "voluntarios",
                interesseService.listarVoluntarios(ongId)
        );

        model.addAttribute(
                "pedidosAjuda",
                interesseService.listarPedidosAjuda(ongId)
        );

        return "ong/pessoas-interessadas";
    }
}
