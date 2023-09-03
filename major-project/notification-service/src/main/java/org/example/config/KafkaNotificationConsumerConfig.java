package org.example.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.dto.UserCreatedPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
public class KafkaNotificationConsumerConfig {

    private static Logger LOGGER = LoggerFactory.getLogger(KafkaNotificationConsumerConfig.class);


    private static ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private JavaMailSender javaMailSender;

    @KafkaListener(topics = "USER_CREATED", groupId = "email")
    public void consumeFromUserCreatedTopic(ConsumerRecord payload) throws JsonProcessingException {
        LOGGER.info("Getting payload from kafka: {}",payload);
        UserCreatedPayload userCreatedPayload = objectMapper.readValue(payload.value().toString(),UserCreatedPayload.class);
        LOGGER.info("Getting userCreatedPayload from kafka: {}",userCreatedPayload);
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("jbdl.ewallet@gmail.com");
        simpleMailMessage.setSubject("Welcome "+userCreatedPayload.getUserName()+"!");
        simpleMailMessage.setTo(userCreatedPayload.getUserEmail());
        simpleMailMessage.setText("Hi "+userCreatedPayload.getUserName()+", Welcome in JBDL-46 wallet world");
        simpleMailMessage.setCc("admin.jbdl46@yopmail.com");
        javaMailSender.send(simpleMailMessage);

    }
}
