package br.com.fiap.conectasocial.controller.web;

import br.com.fiap.conectasocial.dto.auth.CadastroRequest;
import br.com.fiap.conectasocial.service.UsuarioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthWebController {
    private static final Logger log = LoggerFactory.getLogger(AuthWebController.class);
    private final UsuarioService usuarioService;

    public AuthWebController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/registrar")
    public String showRegistro(Model model) {
        if (!model.containsAttribute("cadastro")) model.addAttribute("cadastro", new CadastroRequest());
        return "registrar";
    }

    @PostMapping("/registrar")
    public String registrar(@Valid @ModelAttribute("cadastro") CadastroRequest cadastro,
                            BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("erro", "Confira os campos informados e tente novamente.");
            return "registrar";
        }
        try {
            usuarioService.cadastrar(cadastro);
            return "redirect:/login?sucesso";
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            return "registrar";
        } catch (Exception e) {
            log.error("Falha ao cadastrar usuário no ConectaSocial", e);
            model.addAttribute("erro", "Não foi possível salvar o cadastro. Confira os dados e tente novamente. Se persistir, verifique o console da aplicação.");
            return "registrar";
        }
    }
}
