package com.asan.mail.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String ORDER_EXCHANGE_NAME = "order_exchange";
    public static final String ORDER_QUEUE_NAME = "order_queue";

    @Bean
    public FanoutExchange orderExchange() {
        return new FanoutExchange(ORDER_EXCHANGE_NAME, true, false);
    }

    @Bean
    public Queue orderQueue() {
        return new Queue(ORDER_QUEUE_NAME, true);
    }

    @Bean
    public Binding orderQueueBinding(
            @Qualifier("orderQueue") Queue orderQueue,
            @Qualifier("orderExchange") FanoutExchange orderExchange) {
        return BindingBuilder.bind(orderQueue).to(orderExchange);
    }
}
