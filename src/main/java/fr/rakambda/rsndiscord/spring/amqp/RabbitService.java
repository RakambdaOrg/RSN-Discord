package fr.rakambda.rsndiscord.spring.amqp;

import fr.rakambda.rsndiscord.spring.amqp.message.IDelayedMessage;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.Duration;

@Service
@Slf4j
public class RabbitService{
	private final RabbitTemplate rabbitTemplate;
	private final AmqpNameProvider amqpNameProvider;
	
	@Autowired
	public RabbitService(RabbitTemplate rabbitTemplate, AmqpNameProvider amqpNameProvider){
		this.rabbitTemplate = rabbitTemplate;
		this.amqpNameProvider = amqpNameProvider;
	}
	
	public void scheduleMessage(@NonNull Duration delay, @NonNull IDelayedMessage message){
		scheduleMessage(delay, 5, message);
	}
	
	public void scheduleMessage(@NonNull Duration delay, int retryCount, @NonNull IDelayedMessage message){
		log.info("Delaying by {} message {}", delay, message);
		rabbitTemplate.convertAndSend(amqpNameProvider.getExchangeDelayName(), amqpNameProvider.getRoutingKeyDelay(), message, getMessagePostProcessor(delay, retryCount));
	}
	
	@NonNull
	private MessagePostProcessor getMessagePostProcessor(@NonNull Duration delay, int retryCount){
		return m -> {
			m.getMessageProperties().setHeader("x-delay", delay.toMillis());
			m.getMessageProperties().setHeader(amqpNameProvider.getXRedeliverCountRemainingHeader(), retryCount);
			return m;
		};
	}
}
