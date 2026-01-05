package com.social_network.user_service.kafka.listener;

import com.social_network.user_service.kafka.dto.CreateUserRequest;
import com.social_network.user_service.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventListener {
    private final ObjectMapper objectMapper;
    private final UserProfileService userProfileService;
    @KafkaListener(
            topics = "${kafka.topic.user-events}",
            groupId = "${kafka.topic.user-events}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onMessage(String message,
                          @Header(KafkaHeaders.OFFSET) long offset,
                          Acknowledgment ack) {
        try {
            log.info("Received message: {}", message);
            //Convert to DTO
            CreateUserRequest request = objectMapper.readValue(message, CreateUserRequest.class);
            //Save User Profile
            userProfileService.createUserProfile(request);
            //Commit message
            ack.acknowledge();
        }catch (Exception e){
            log.error("Error processing message UserEventListener, offset={}", offset, e);
        }
    }
}
