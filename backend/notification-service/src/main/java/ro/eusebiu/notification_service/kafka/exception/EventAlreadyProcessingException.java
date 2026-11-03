
package ro.eusebiu.notification_service.kafka.exception;

public class EventAlreadyProcessingException extends RuntimeException {

    public EventAlreadyProcessingException(String messageId) {
        super("Event is already being processed: " + messageId);
    }
}
