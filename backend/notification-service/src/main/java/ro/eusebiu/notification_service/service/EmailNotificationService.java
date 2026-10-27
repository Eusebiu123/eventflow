package ro.eusebiu.notification_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailNotificationService {

    private final JavaMailSender mailSender;

    public void sendOrderCreatedEmail(
            String email,
            Long orderId,
            String eventTitle,
            Integer ticketsCount
    ) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("noreply@eventflow.local");
        message.setTo(email);
        message.setSubject("EventFlow - Order Created");

        message.setText(
                "Hello!\n\n" +
                        "Your order #" + orderId + " has been created.\n" +
                        "Event: " + eventTitle + "\n" +
                        "Tickets: " + ticketsCount + "\n" +
                        "Status: PENDING\n\n" +
                        "Thank you for using EventFlow!"
        );

        mailSender.send(message);
    }
}