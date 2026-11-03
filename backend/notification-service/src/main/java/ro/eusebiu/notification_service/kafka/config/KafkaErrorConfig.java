
package ro.eusebiu.notification_service.kafka.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.util.backoff.FixedBackOff;
import ro.eusebiu.notification_service.kafka.exception.EventAlreadyProcessingException;

@Configuration
@RequiredArgsConstructor
public class KafkaErrorConfig {

    @Bean
    public DefaultErrorHandler kafkaErrorHandler(
            KafkaTemplate<Object, Object> kafkaTemplate
    ) {

        DeadLetterPublishingRecoverer recoverer =
                new DeadLetterPublishingRecoverer(
                        kafkaTemplate,
                        (record, exception) ->
                                new TopicPartition(
                                        record.topic() + ".DLT",
                                        record.partition()
                                )
                );

        DefaultErrorHandler errorHandler =
                new DefaultErrorHandler(
                        recoverer,
                        new FixedBackOff(2000L, 3L)
                );

        errorHandler.setBackOffFunction(
                (record, exception) -> {
                    if (exception instanceof EventAlreadyProcessingException) {
                        return new FixedBackOff(5000L, 15L);
                    }

                    return new FixedBackOff(2000L, 3L);
                }
        );

        return errorHandler;
    }
}
