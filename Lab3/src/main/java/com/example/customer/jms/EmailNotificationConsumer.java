package com.example.customer.jms;

import com.example.customer.dto.WelcomeEmailMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationConsumer.class);

    @JmsListener(destination = "${app.queue.email}", containerFactory = "queueListenerFactory")
    public void receiveWelcomeEmail(WelcomeEmailMessage message) {
        log.info("Получено сообщение из очереди: клиент ID={}, email={}", message.getCustomerId(), message.getEmail());
        try {
            Thread.sleep(2000);
            log.info("[ИМИТАЦИЯ] Приветственное письмо отправлено на адрес: {} (клиент: {})",
                    message.getEmail(), message.getFirstName());
            log.info("Обработка email завершена для клиента ID: {}", message.getCustomerId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Ошибка при обработке email-сообщения", e);
        }
    }
}
