package ro.eusebiu.notification_service.kafka.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.DelegatingByTypeSerializer;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaDltProducerConfig {

    @Bean
    public KafkaTemplate<Object, Object> kafkaTemplate(
            @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers
    ) {
        Map<String, Object> config = new HashMap<>();

        config.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers
        );

        Map<Class<?>, org.apache.kafka.common.serialization.Serializer<?>> serializers =
                new HashMap<>();

        serializers.put(String.class, new StringSerializer());
        serializers.put(byte[].class, new ByteArraySerializer());
        serializers.put(Object.class, new JacksonJsonSerializer<>());

        ProducerFactory<Object, Object> producerFactory =
                new DefaultKafkaProducerFactory<>(
                        config,
                        new DelegatingByTypeSerializer(serializers, true),
                        new DelegatingByTypeSerializer(serializers, true)
                );

        return new KafkaTemplate<>(producerFactory);
    }
}