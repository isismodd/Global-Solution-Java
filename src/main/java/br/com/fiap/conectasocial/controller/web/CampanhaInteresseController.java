package br.com.fiap.conectasocial.controller.web;

import br.com.fiap.conectasocial.model.Usuario;
import br.com.fiap.conectasocial.repository.CampanhaRepository;
import br.com.fiap.conectasocial.service.CampanhaInteresseService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ong/interesses")
public class CampanhaInteresseController {

    private final CampanhaInteresseService interesseService;
    private final CampanhaRepository campanhaRepository;

    public CampanhaInteresseController(
            CampanhaInteresseService interesseService,
            CampanhaRepository campanhaRepository) {
        this.interesseService = interesseService;
        this.campanhaRepository = campanhaRepository;
    }

    @GetMapping
    public String listarInteresses(Authentication authentication, Model model) {

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