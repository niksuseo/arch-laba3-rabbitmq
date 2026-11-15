
package com.misis.archapp.user.publisher;

import com.misis.archapp.contract.configuration.RabbitConfiguration;
import com.misis.archapp.contract.dto.UserCreatedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public EventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishUserCreated(UserCreatedEvent event) {
        rabbitTemplate.convertAndSend(
            RabbitConfiguration.USER_EXCHANGE,
            "user.created",
            event
        );
    }
}
