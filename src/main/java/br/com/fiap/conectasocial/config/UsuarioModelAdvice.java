
package br.com.fiap.conectasocial.config;

import br.com.fiap.conectasocial.model.Usuario;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class UsuarioModelAdvice {

    @ModelAttribute
    public void adicionarDadosUsuario(
            Authentication authentication,
            Model model) {

        if (authentication != null
                && authentication.getPrincipal() instanceof Usuario usuario) {

            model.addAttribute("usuario", usuario);
            model.addAttribute("perfil", usuario.getPerfil());
            model.addAttribute("nomeExibicao", usuario.getNome());

        } else {
            model.addAttribute("usuario", null);
            model.addAttribute("perfil", null);
            model.addAttribute("nomeExibicao", "Visitante");
        }
    }
}
