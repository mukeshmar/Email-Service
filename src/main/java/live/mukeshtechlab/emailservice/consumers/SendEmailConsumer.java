package live.mukeshtechlab.emailservice.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import live.mukeshtechlab.emailservice.dtos.SendEmailDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

@Service
public class SendEmailConsumer {

    @Value("${sender.email}")
    private String senderEmail;

    private ObjectMapper objectMapper;
    private JavaMailSender javaMailSender;

    public SendEmailConsumer(
            ObjectMapper objectMapper,
            JavaMailSender javaMailSender
    ){
        this.objectMapper = objectMapper;
        this.javaMailSender = javaMailSender;
    }

    @KafkaListener(topics = "sendEmail", groupId = "emailService")
    public void handleSendEmailEvent(String message) throws JsonProcessingException {
        SendEmailDto sendEmailDto = objectMapper.readValue(message, SendEmailDto.class);

        String to = sendEmailDto.getTo();
        String subject = sendEmailDto.getSubject();
        String body = sendEmailDto.getBody();

        // Send SMTP Email
        // Creating a simple mail message
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        // Setting up the necessary details
        mailMessage.setFrom(senderEmail);
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(body);

        // Sending the mail
        javaMailSender.send(mailMessage);
        System.out.println("Successfully Sent Email");
    }
}
