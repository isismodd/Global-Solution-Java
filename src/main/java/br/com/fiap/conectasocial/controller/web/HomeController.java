package br.com.fiap.conectasocial.controller.web;

import br.com.fiap.conectasocial.model.Usuario;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
public class HomeController {
    @GetMapping({"/", "/home"})
    public String home(Authentication authentication, Model model) {
        if (authentication != null && authentication.getPrincipal() instanceof Usuario usuario) {
            model.addAttribute("usuario", usuario);
            model.addAttribute("perfil", usuario.getPerfil());
            model.addAttribute("nomeExibicao", usuario.getNome());
        } else {
            model.addAttribute("perfil", "VOLUNTARIO");
            model.addAttribute("nomeExibicao", "Visitante");
        }

        // Conteúdo demonstrativo para a primeira versão visual do feed.
        model.addAttribute("campanhas", List.of(
                Map.of("ong", "Instituto Patas Amigas", "categoria", "Ajuda animal", "local", "São Paulo, SP", "titulo", "Um lar temporário faz toda a diferença", "descricao", "Estamos buscando pessoas dispostas a acolher cães resgatados enquanto eles aguardam uma família definitiva.", "tag", "Voluntariado", "inicial", "PA", "cor", "peach", "fixada", true),
                Map.of("ong", "Projeto Sementes do Amanhã", "categoria", "Educação", "local", "Guarulhos, SP", "titulo", "Doe seu tempo: reforço escolar para crianças", "descricao", "Precisamos de voluntários para apoiar crianças com atividades de leitura, matemática e aprendizado criativo.", "tag", "Educação", "inicial", "SA", "cor", "yellow", "fixada", false),
                Map.of("ong", "Cozinha Solidária Esperança", "categoria", "Alimentação", "local", "São Paulo, SP", "titulo", "Mais refeições, mais dignidade", "descricao", "Nossa cozinha comunitária prepara refeições para famílias em situação de vulnerabilidade. Conheça a iniciativa.", "tag", "Apoio social", "inicial", "CE", "cor", "green", "fixada", false)
        ));
        model.addAttribute("categorias", List.of("Todas as causas", "Ajuda animal", "Educação", "Alimentação", "Saúde", "Moradia", "Apoio social"));
        return "home";
    }
}
