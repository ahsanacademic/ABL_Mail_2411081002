package com.asan.mail.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.asan.mail.config.RabbitMQConfig;
import com.asan.mail.model.OrderMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Service
public class OrderMailListener {

    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Autowired
    private OrderMailSender orderMailSender;

    @RabbitListener(queues = RabbitMQConfig.ORDER_QUEUE_NAME)
    public void receiveOrder(String message) {
        try {
            OrderMessage order = objectMapper.readValue(message, OrderMessage.class);
            System.out.println("Received order: " + message);
            orderMailSender.sendOrderMail(order);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to read order message", e);
        }
    }
}
