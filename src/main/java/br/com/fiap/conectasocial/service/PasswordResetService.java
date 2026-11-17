
package br.com.fiap.conectasocial.service;

import br.com.fiap.conectasocial.model.Usuario;
import br.com.fiap.conectasocial.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;

@Service
public class PasswordResetService {

    private final UsuarioRepository usuarioRepository;
    private final JdbcTemplate jdbc;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;
    private final String baseUrl;
    private final String emailRemetente;

    private final SecureRandom secureRandom = new SecureRandom();

    public PasswordResetService(
            UsuarioRepository usuarioRepository,
            JdbcTemplate jdbc,
            JavaMailSender mailSender,
            PasswordEncoder passwordEncoder,
            @Value("${app.base-url}") String baseUrl,
            @Value("${spring.mail.username}") String emailRemetente) {

        this.usuarioRepository = usuarioRepository;
        this.jdbc = jdbc;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
        this.baseUrl = baseUrl;
        this.emailRemetente = emailRemetente;
    }

    @Transactional
    public void solicitarRecuperacao(String email) {
        if (email == null || email.isBlank()) {
            return;
        }

        Optional<Usuario> resultado =
                usuarioRepository.buscarPorEmail(email.trim().toLowerCase());

        if (resultado.isEmpty()) {
            return;
        }

        Usuario usuario = resultado.get();

        if (!Boolean.TRUE.equals(usuario.getAtivo())) {
            return;
        }

        if (baseUrl == null || baseUrl.isBlank()
                || emailRemetente == null || emailRemetente.isBlank()) {
            throw new IllegalStateException(
                    "Configure APP_BASE_URL e EMAIL_USERNAME nas variáveis de ambiente.");
        }

        // Remove tokens anteriores do mesmo usuário.
        jdbc.update(
                "DELETE FROM PASSWORD_RESET_TOKENS WHERE USUARIO_ID = ?",
                usuario.getId()
        );

        // Gera um token aleatório seguro.
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);

        String token = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);

        String tokenHash = gerarHash(token);

        // Salva somente o hash do token. Validade: 30 minutos.
        jdbc.update("""
                INSERT INTO PASSWORD_RESET_TOKENS
                    (USUARIO_ID, TOKEN_HASH, EXPIRA_EM, USADO, CRIADO_EM)
                VALUES (?, ?, SYSTIMESTAMP + INTERVAL '30' MINUTE, 0, SYSTIMESTAMP)
                """,
                usuario.getId(),
                tokenHash
        );

        String link = baseUrl.replaceAll("/+$", "")
                + "/redefinir-senha?token=" + token;

        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setFrom(emailRemetente);
        mensagem.setTo(usuario.getEmail());
        mensagem.setSubject("Recuperação de senha - ConectaSocial");
        mensagem.setText(
                "Olá, " + usuario.getNome() + "!\n\n"
                        + "Recebemos uma solicitação para redefinir sua senha no ConectaSocial.\n\n"
                        + "Acesse o link abaixo para cadastrar uma nova senha:\n"
                        + link + "\n\n"
                        + "Este link é válido por 30 minutos e só pode ser utilizado uma vez.\n\n"
                        + "Se você não solicitou a recuperação, ignore este e-mail.\n\n"
                        + "Equipe ConectaSocial"
        );

        mailSender.send(mensagem);
    }

    public boolean tokenValido(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }

        String hash = gerarHash(token);

        var resultados = jdbc.query("""
            SELECT ID, USADO, EXPIRA_EM,
                   CASE
                       WHEN EXPIRA_EM > CAST(SYSTIMESTAMP AS TIMESTAMP) THEN 1
                       ELSE 0
                   END AS DENTRO_DO_PRAZO
            FROM PASSWORD_RESET_TOKENS
            WHERE TOKEN_HASH = ?
            """,
                (rs, rowNum) -> {
                    System.out.println("TOKEN ENCONTRADO:");
                    System.out.println("ID: " + rs.getLong("ID"));
                    System.out.println("USADO: " + rs.getInt("USADO"));
                    System.out.println("EXPIRA EM: " + rs.getTimestamp("EXPIRA_EM"));
                    System.out.println("DENTRO DO PRAZO: " + rs.getInt("DENTRO_DO_PRAZO"));

                    return rs.getInt("USADO") == 0
                            && rs.getInt("DENTRO_DO_PRAZO") == 1;
                },
                hash
        );

        return !resultados.isEmpty() && resultados.get(0);
    }

    @Transactional
    public void redefinirSenha(String token, String novaSenha, String confirmarSenha) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Link de recuperação inválido.");
        }

        if (novaSenha == null || novaSenha.isBlank()) {
            throw new IllegalArgumentException("Informe a nova senha.");
        }

        if (novaSenha.length() < 8) {
            throw new IllegalArgumentException(
                    "A senha deve possuir pelo menos 8 caracteres.");
        }

        if (!novaSenha.equals(confirmarSenha)) {
            throw new IllegalArgumentException("As senhas não coincidem.");
        }

        String hash = gerarHash(token);

        var tokens = jdbc.query("""
                SELECT ID, USUARIO_ID
                FROM PASSWORD_RESET_TOKENS
                WHERE TOKEN_HASH = ?
                  AND USADO = 0
                  AND EXPIRA_EM > CAST(SYSTIMESTAMP AS TIMESTAMP)
                FOR UPDATE
                """,
                (rs, rowNum) -> new TokenInfo(
                        rs.getLong("ID"),
                        rs.getLong("USUARIO_ID")
                ),
                hash
        );

        if (tokens.isEmpty()) {
            throw new IllegalArgumentException(
                    "Link inválido ou expirado. Solicite uma nova recuperação.");
        }

        TokenInfo tokenInfo = tokens.get(0);

        int atualizado = jdbc.update(
                "UPDATE USUARIOS SET SENHA = ? WHERE ID = ? AND ATIVO = 1",
                passwordEncoder.encode(novaSenha),
                tokenInfo.usuarioId()
        );

        if (atualizado != 1) {
            throw new IllegalArgumentException(
                    "Não foi possível atualizar a senha deste usuário.");
        }

        jdbc.update(
                "UPDATE PASSWORD_RESET_TOKENS SET USADO = 1 WHERE ID = ?",
                tokenInfo.id()
        );

        // Invalida outros tokens que eventualmente existam para o usuário.
        jdbc.update("""
                UPDATE PASSWORD_RESET_TOKENS
                SET USADO = 1
                WHERE USUARIO_ID = ?
                  AND ID <> ?
                  AND USADO = 0
                """,
                tokenInfo.usuarioId(),
                tokenInfo.id()
        );
    }

    private String gerarHash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new IllegalStateException("Erro ao gerar hash do token.", e);
        }
    }

    private record TokenInfo(Long id, Long usuarioId) {}
}
