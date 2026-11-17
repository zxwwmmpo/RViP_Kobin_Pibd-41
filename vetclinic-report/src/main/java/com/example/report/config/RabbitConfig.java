package com.example.report.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;

@Configuration
public class RabbitConfig {
    public static final String EXCHANGE = "reports.exchange";
    public static final String QUEUE = "reports.generate";
    public static final String ROUTING_KEY = "reports.generate";

    public static final String DEAD_LETTER_EXCHANGE = "reports.dlx";
    public static final String DEAD_LETTER_QUEUE = "reports.failed";
    public static final String DEAD_LETTER_ROUTING_KEY = "reports.failed";

    @Bean
    public DirectExchange reportsExchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue reportsQueue() {
        return QueueBuilder.durable(QUEUE)
                .deadLetterExchange(DEAD_LETTER_EXCHANGE)
                .deadLetterRoutingKey(DEAD_LETTER_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding reportsBinding() {
        return BindingBuilder.bind(reportsQueue())
                .to(reportsExchange())
                .with(ROUTING_KEY);
    }

    @Bean
    public DirectExchange reportsDeadLetterExchange() {
        return new DirectExchange(DEAD_LETTER_EXCHANGE, true, false);
    }

    @Bean
    public Queue reportsDeadLetterQueue() {
        return QueueBuilder.durable(DEAD_LETTER_QUEUE).build();
    }

    @Bean
    public Binding reportsDeadLetterBinding() {
        return BindingBuilder.bind(reportsDeadLetterQueue())
                .to(reportsDeadLetterExchange())
                .with(DEAD_LETTER_ROUTING_KEY);
    }

    @Bean
    public JacksonJsonMessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
