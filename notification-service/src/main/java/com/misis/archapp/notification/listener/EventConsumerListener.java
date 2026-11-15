
package com.misis.archapp.notification.listener;

import com.misis.archapp.contract.configuration.RabbitConfiguration;
import com.misis.archapp.contract.dto.UserCreatedEvent;
import com.misis.archapp.notification.service.NotificationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class EventConsumerListener {

    private final NotificationService notificationService;

    public EventConsumerListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @RabbitListener(queues = RabbitConfiguration.USER_QUEUE)
    public void handleUserCreated(UserCreatedEvent event) {
        notificationService.sendNotification(event);
    }
}
