
package br.com.fiap.conectasocial.service;

import br.com.fiap.conectasocial.model.Usuario;
import br.com.fiap.conectasocial.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Locale;

@Service
public class PerfilService {

    private final UsuarioRepository usuarioRepository;

    public PerfilService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.buscarPorId(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Usuário não encontrado."));
    }

    public void atualizarDados(
            Long id,
            String nome,
            String email,
            String telefone) {

        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Informe seu nome.");
        }

        if (nome.trim().length() > 120) {
            throw new IllegalArgumentException("O nome deve ter até 120 caracteres.");
        }

        if (email == null || email.isBlank() ||
                !email.trim().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("Informe um e-mail válido.");
        }

        if (email.trim().length() > 150) {
            throw new IllegalArgumentException("O e-mail deve ter até 150 caracteres.");
        }

        if (telefone != null && telefone.length() > 20) {
            throw new IllegalArgumentException("O telefone deve ter até 20 caracteres.");
        }

        String emailNormalizado = email.trim().toLowerCase(Locale.ROOT);

        if (usuarioRepository.emailJaCadastrado(emailNormalizado, id)) {
            throw new IllegalArgumentException(
                    "Este e-mail já está cadastrado por outro usuário.");
        }

        usuarioRepository.atualizarDadosPerfil(
                id,
                nome.trim(),
                emailNormalizado,
                telefone == null || telefone.isBlank() ? null : telefone.trim()
        );
    }

    public void atualizarFoto(Long id, MultipartFile arquivo) throws IOException {

        if (arquivo == null || arquivo.isEmpty()) {
            throw new IllegalArgumentException("Selecione uma imagem.");
        }

        if (arquivo.getSize() > 2 * 1024 * 1024) {
            throw new IllegalArgumentException(
                    "A imagem deve ter no máximo 2 MB.");
        }

        String tipo = arquivo.getContentType();

        if (tipo == null ||
                !tipo.matches("image/(jpeg|png|webp)")) {
            throw new IllegalArgumentException(
                    "Formato inválido. Envie uma imagem JPG, PNG ou WebP.");
        }

        byte[] imagem = arquivo.getBytes();

        if (!imagemValida(imagem, tipo)) {
            throw new IllegalArgumentException("O arquivo enviado não é uma imagem válida.");
        }

        usuarioRepository.atualizarFotoPerfil(id, imagem);
    }

    private boolean imagemValida(byte[] imagem, String tipo) {
        if (imagem.length < 12) {
            return false;
        }

        return switch (tipo) {
            case "image/jpeg" ->
                    (imagem[0] & 0xFF) == 0xFF &&
                            (imagem[1] & 0xFF) == 0xD8 &&
                            (imagem[2] & 0xFF) == 0xFF;

            case "image/png" ->
                    (imagem[0] & 0xFF) == 0x89 &&
                            imagem[1] == 0x50 &&
                            imagem[2] == 0x4E &&
                            imagem[3] == 0x47;

            case "image/webp" ->
                    imagem[0] == 'R' &&
                            imagem[1] == 'I' &&
                            imagem[2] == 'F' &&
                            imagem[3] == 'F' &&
                            imagem[8] == 'W' &&
                            imagem[9] == 'E' &&
                            imagem[10] == 'B' &&
                            imagem[11] == 'P';

            default -> false;
        };
    }
}
