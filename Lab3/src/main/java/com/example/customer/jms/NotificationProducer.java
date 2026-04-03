package com.example.customer.jms;

import com.example.customer.dto.WelcomeEmailMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotificationProducer {

    private static final Logger log = LoggerFactory.getLogger(NotificationProducer.class);

    private final JmsTemplate jmsTemplate;

    @Value("${app.queue.email}")
    private String emailQueue;

    @Autowired
    public NotificationProducer(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void sendWelcomeEmail(Long customerId, String email, String firstName) {
        WelcomeEmailMessage message = new WelcomeEmailMessage(customerId, email, firstName);
        log.info("Отправка сообщения в очередь {}: customerId={}", emailQueue, customerId);
        jmsTemplate.convertAndSend(emailQueue, message, postProcessor -> {
            postProcessor.setStringProperty("messageType", "welcome-email");
            postProcessor.setLongProperty("customerId", customerId);
            return postProcessor;
        });
        log.info("Сообщение успешно помещено в очередь для клиента ID: {}", customerId);
    }
}
