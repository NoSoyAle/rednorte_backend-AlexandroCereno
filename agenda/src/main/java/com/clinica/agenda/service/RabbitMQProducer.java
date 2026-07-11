package com.clinica.agenda.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.clinica.agenda.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RabbitMQProducer {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQProducer.class);

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public RabbitMQProducer(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public void enviarNotificacion(Map<String, String> notificacion) {
        try {
            String mensaje = objectMapper.writeValueAsString(notificacion);
            rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, mensaje);
            logger.info("Mensaje enviado a cola 'notificaciones': " + mensaje);
        } catch (Exception e) {
            logger.error("Error al enviar notificación a RabbitMQ: " + e.getMessage());
        }
    }
}
