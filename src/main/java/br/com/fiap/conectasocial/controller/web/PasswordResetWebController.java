
package br.com.fiap.conectasocial.controller.web;


import br.com.fiap.conectasocial.service.PasswordResetService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PasswordResetWebController {

    private final PasswordResetService passwordResetService;

    private static final Logger log =
            LoggerFactory.getLogger(PasswordResetWebController.class);

    public PasswordResetWebController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    // Exibe a tela para informar o e-mail.
    @GetMapping("/recuperar-senha")
    public String exibirRecuperacao() {
        return "recuperar-senha";
    }

    // Recebe o e-mail e solicita o envio do link.
    @PostMapping("/recuperar-senha")
    public String solicitarRecuperacao(
            @RequestParam("email") String email,
            RedirectAttributes redirectAttributes) {

        try {
            passwordResetService.solicitarRecuperacao(email);

            redirectAttributes.addFlashAttribute(
                    "mensagem",
                    "Se o e-mail estiver cadastrado, você receberá um link para redefinir sua senha."
            );

        } catch (Exception e) {
            log.error("Erro ao solicitar recuperação de senha", e);

            redirectAttributes.addFlashAttribute(
                    "erro",
                    "Não foi possível enviar o e-mail de recuperação. Tente novamente mais tarde."
            );
        }

        return "redirect:/recuperar-senha";
    }

    // Exibe a tela para cadastrar a nova senha.
    @GetMapping("/redefinir-senha")
    public String exibirRedefinicao(
            @RequestParam(value = "token", required = false) String token,
            Model model) {

        if (token == null || !passwordResetService.tokenValido(token)) {
            model.addAttribute(
                    "erro",
                    "Este link é inválido ou expirou. Solicite uma nova recuperação de senha."
            );
            return "redefinir-senha";
        }

        model.addAttribute("token", token);
        return "redefinir-senha";
    }

    // Recebe e salva a nova senha.
    @PostMapping("/redefinir-senha")
    public String redefinirSenha(
            @RequestParam("token") String token,
            @RequestParam("novaSenha") String novaSenha,
            @RequestParam("confirmarSenha") String confirmarSenha,
            RedirectAttributes redirectAttributes,
            Model model) {

        try {
            passwordResetService.redefinirSenha(
                    token,
                    novaSenha,
                    confirmarSenha
            );

            redirectAttributes.addFlashAttribute(
                    "sucesso",
                    "Sua senha foi alterada com sucesso! Faça login com a nova senha."
            );

            return "redirect:/login";

        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("token", token);
            return "redefinir-senha";

        } catch (Exception e) {
            model.addAttribute(
                    "erro",
                    "Não foi possível redefinir sua senha. Tente novamente."
            );
            model.addAttribute("token", token);
            return "redefinir-senha";
        }
    }
}
