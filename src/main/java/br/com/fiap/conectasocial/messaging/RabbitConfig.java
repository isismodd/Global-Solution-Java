package br.com.fiap.conectasocial.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "conectasocial.eventos";
    public static final String ROUTING_KEY_INTERESSE = "interesse.registrado";
    public static final String QUEUE_INTERESSE = "conectasocial.interesse.registrado";

    @Bean
    public DirectExchange eventosExchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue interesseRegistradoQueue() {
        return QueueBuilder.durable(QUEUE_INTERESSE).build();
    }

    @Bean
    public Binding interesseRegistradoBinding(Queue interesseRegistradoQueue,
                                              DirectExchange eventosExchange) {
        return BindingBuilder.bind(interesseRegistradoQueue)
                .to(eventosExchange)
                .with(ROUTING_KEY_INTERESSE);
    }

    /** Serializa os eventos em JSON (legível no painel do RabbitMQ). */
    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTrustedPackages("br.com.fiap.conectasocial.messaging");
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }
}
