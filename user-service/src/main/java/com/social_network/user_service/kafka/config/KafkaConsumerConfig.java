package com.social_network.user_service.kafka.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {

        Map<String, Object> props = new HashMap<>();
        //Cấu hình các config
        if(true) {
            // Kafka broker
            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            // Deserialize key (aggregate_id)
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            // Deserialize payload JSON
            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            /**
             * Group ID
             * - Mỗi service = 1 group
             * - Scale bằng cách tăng consumer instance
             */
            props.put(ConsumerConfig.GROUP_ID_CONFIG, "user-service");
            /**
             * Nếu chưa có offset:
             * - earliest = đọc từ đầu
             */
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            /**
             * TẮT auto commit
             * - Commit offset bằng tay
             * - Chỉ commit khi DB thành công
             */
            props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
            /**
             * Chỉ đọc message đã commit transaction
             * - Dùng khi producer bật idempotence / transaction
             */
            props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        }
        return new DefaultKafkaConsumerFactory<>(props);
    }

    /**
     * KafkaListenerContainerFactory
     * - Config retry + DLQ + manual commit
     */
    //Cấu hình Cơ chế cho Kafka Listener
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(ConsumerFactory<String, String> consumerFactory, KafkaTemplate<String, String> kafkaTemplate) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        /**
         * MANUAL ACK
         * - Chủ động commit offset
         */
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        /**
         * ErrorHandler
         * - Retry 3 lần
         * - Mỗi lần cách 3 giây
         * - Fail nữa thì đẩy sang DLQ
         */
        DefaultErrorHandler errorHandler =
                new DefaultErrorHandler(
                        new DeadLetterPublishingRecoverer(
                                kafkaTemplate,
                                (record, ex) ->
                                        new TopicPartition(record.topic() + ".DLQ", record.partition())
                        ),
                        new FixedBackOff(3000L, 3)
                );
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }
}

