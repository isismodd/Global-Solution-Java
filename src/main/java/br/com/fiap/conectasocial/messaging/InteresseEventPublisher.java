package br.com.fiap.conectasocial.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * PRODUTOR: publica o evento no RabbitMQ somente DEPOIS que o interesse foi
 * gravado no banco (commit). Se o broker estiver fora do ar, a API continua
 * funcionando e o problema só é registrado no log.
 */
@Component
public class InteresseEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(InteresseEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public InteresseEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publicar(InteresseRegistradoEvent evento) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitConfig.EXCHANGE, RabbitConfig.ROUTING_KEY_INTERESSE, evento);
            log.info("[RabbitMQ] Evento publicado: interesse {} na campanha {}",
                    evento.interesseId(), evento.campanhaId());
        } catch (AmqpException e) {
            log.error("[RabbitMQ] Não foi possível publicar o evento do interesse {}: {}",
                    evento.interesseId(), e.getMessage());
        }
    }
}
