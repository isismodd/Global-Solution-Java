
package br.com.fiap.conectasocial.controller.web;

import br.com.fiap.conectasocial.model.Usuario;
import br.com.fiap.conectasocial.service.CampanhaService;
import br.com.fiap.conectasocial.service.CampanhaInteresseService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    private final CampanhaService campanhaService;
    private final CampanhaInteresseService interesseService;

    public HomeController(
            CampanhaService campanhaService,
            CampanhaInteresseService interesseService) {
        this.campanhaService = campanhaService;
        this.interesseService = interesseService;
    }

    @GetMapping({"/", "/home"})
    public String home(Authentication authentication, Model model) {

        Usuario usuarioLogado = null;

        if (authentication != null &&
                authentication.getPrincipal() instanceof Usuario usuario) {

            usuarioLogado = usuario;

            model.addAttribute("usuario", usuario);
            model.addAttribute("perfil", usuario.getPerfil());
            model.addAttribute("nomeExibicao", usuario.getNome());

        } else {
            model.addAttribute("perfil", "VOLUNTARIO");
            model.addAttribute("nomeExibicao", "Visitante");
        }

        List<Map<String, Object>> campanhas =
                campanhaService.listarCampanhasDoFeed();

        for (Map<String, Object> campanha : campanhas) {

            campanha.put("temImagem",
                    ((Number) campanha.get("TEM_IMAGEM")).intValue() == 1);

            campanha.put("temFotoOng",
                    ((Number) campanha.get("TEM_FOTO_ONG")).intValue() == 1);

            String nomeOng = String.valueOf(campanha.get("NOME_ONG"));

            campanha.put("ong", nomeOng);
            campanha.put("tag", campanha.get("CATEGORIA"));
            campanha.put("inicial", obterIniciais(nomeOng));
            campanha.put("cor", obterCor(campanha.get("CATEGORIA")));
            campanha.put("fixada", false);

            boolean jaDemonstrouInteresse = false;

            if (usuarioLogado != null &&
                    ("VOLUNTARIO".equals(usuarioLogado.getPerfil()) ||
                            "BENEFICIARIO".equals(usuarioLogado.getPerfil()))) {

                Number idCampanha = (Number) campanha.get("ID");

                jaDemonstrouInteresse =
                        interesseService.jaDemonstrouInteresse(
                                idCampanha.longValue(),
                                usuarioLogado.getId()
                        );
            }

            campanha.put("jaDemonstrouInteresse", jaDemonstrouInteresse);
        }

        model.addAttribute("campanhas", campanhas);

        model.addAttribute("categorias", List.of(
                "Todas as causas",
                "Ajuda animal",
                "Educação",
                "Alimentação",
                "Saúde",
                "Moradia",
                "Apoio social"
        ));

        return "home";
    }

    private String obterIniciais(String nome) {
        if (nome == null || nome.isBlank()) {
            return "ONG";
        }

        String[] partes = nome.trim().split("\\s+");

        if (partes.length == 1) {
            return partes[0].substring(
                    0, Math.min(2, partes[0].length())
            ).toUpperCase();
        }

        return (partes[0].charAt(0) +
                partes[1].substring(0, 1)).toUpperCase();
    }

    private String obterCor(Object categoria) {
        if (categoria == null) {
            return "peach";
        }

        return switch (categoria.toString().toLowerCase()) {
            case "educação" -> "yellow";
            case "alimentação" -> "green";
            case "saúde" -> "peach";
            default -> "peach";
        };
    }
}
