package br.com.fiap.conectasocial.messaging;

import br.com.fiap.conectasocial.api.entity.TipoInteresse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * CONSUMIDOR: recebe o evento de forma assíncrona e avisa a ONG por e-mail.
 * Se o envio falhar, o Spring AMQP tenta novamente (3 tentativas, ver application.properties).
 */
@Component
public class NotificacaoInteresseConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificacaoInteresseConsumer.class);

    private final JavaMailSender mailSender;
    private final String remetente;

    public NotificacaoInteresseConsumer(JavaMailSender mailSender,
                                        @Value("${spring.mail.username:}") String remetente) {
        this.mailSender = mailSender;
        this.remetente = remetente;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_INTERESSE)
    public void receber(InteresseRegistradoEvent evento) {
        log.info("[RabbitMQ] Evento recebido: interesse {} na campanha '{}' (ONG {})",
                evento.interesseId(), evento.tituloCampanha(), evento.nomeOng());

        if (evento.emailOng() == null || evento.emailOng().isBlank()) {
            log.warn("[RabbitMQ] ONG sem e-mail, notificação ignorada.");
            return;
        }

        String acao = evento.tipoInteresse() == TipoInteresse.VOLUNTARIAR
                ? "se oferecer como voluntário(a)"
                : "pedir ajuda";

        SimpleMailMessage mensagem = new SimpleMailMessage();
        if (!remetente.isBlank()) {
            mensagem.setFrom(remetente);
        }
        mensagem.setTo(evento.emailOng());
        mensagem.setSubject("Novo interessado na campanha: " + evento.tituloCampanha());
        mensagem.setText("""
                Olá, %s!

                %s acabou de %s na campanha "%s".

                Acesse o ConectaSocial para ver os detalhes e entrar em contato.

                Equipe ConectaSocial
                """.formatted(evento.nomeOng(), evento.nomeUsuario(), acao, evento.tituloCampanha()));

        mailSender.send(mensagem);
        log.info("[RabbitMQ] E-mail enviado para a ONG {}", evento.emailOng());
    }
}
