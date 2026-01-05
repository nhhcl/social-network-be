package com.social_network.user_service.kafka.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * ProducerFactory
     * - Tạo Kafka Producer
     * - Dùng để publish message (ở đây là DLQ)
     */
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        // Địa chỉ Kafka broker
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        // Serialize key (aggregate_id)
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        // Serialize payload JSON
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        /**
         * ACKS = all
         * - Chỉ coi là gửi thành công khi:
         *   leader + replica đều ghi xong
         * - BẮT BUỘC cho hệ thống event-driven
         */
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        /**
         * Idempotent producer
         * - Tránh gửi trùng message
         * - BẮT BUỘC khi dùng retry
         */
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        /**
         * Retry vô hạn
         * - Kafka tự retry khi lỗi tạm thời
         * - Không ảnh hưởng DB
         */
        props.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
        /**
         * Giữ thứ tự message
         * - <= 5 là điều kiện Kafka bắt buộc khi idempotence=true
         */
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);

        return new DefaultKafkaProducerFactory<>(props);
    }

    /**
     * KafkaTemplate
     * - API cấp cao để gửi message
     */
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(
            ProducerFactory<String, String> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}
