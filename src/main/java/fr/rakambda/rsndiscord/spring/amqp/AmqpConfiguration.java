package fr.rakambda.rsndiscord.spring.amqp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;
import java.util.Map;

@Configuration
@EnableRabbit
public class AmqpConfiguration{
	private final AmqpNameProvider amqpNameProvider;
	
	@Autowired
	public AmqpConfiguration(AmqpNameProvider amqpNameProvider){
		this.amqpNameProvider = amqpNameProvider;
	}
	
	@Bean
	@Qualifier("delayQueue")
	public Queue delayQueue(){
		return new Queue(amqpNameProvider.getQueueDelayName(), true);
	}
	
	@Bean
	@Qualifier("delayExchange")
	public CustomExchange delayExchange(){
		var args = Map.<String, Object> of("x-delayed-type", "direct");
		return new CustomExchange(amqpNameProvider.getExchangeDelayName(), "x-delayed-message", true, false, args);
	}
	
	@Bean
	public Binding delayBinding(@Qualifier("delayQueue") Queue testeQueue, @Qualifier("delayExchange") Exchange exchange){
		return BindingBuilder.bind(testeQueue).to(exchange).with(amqpNameProvider.getRoutingKeyDelay()).noargs();
	}
	
	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter messageConverter){
		var backOffPolicy = new ExponentialBackOffPolicy();
		backOffPolicy.setInitialInterval(1000);
		backOffPolicy.setMultiplier(10);
		backOffPolicy.setMaxInterval(60000);
		
		var retryTemplate = new RetryTemplate();
		retryTemplate.setBackOffPolicy(backOffPolicy);
		
		var rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setMessageConverter(messageConverter);
		rabbitTemplate.setRetryTemplate(retryTemplate);
		return rabbitTemplate;
	}
	
	@Bean
	public Jackson2JsonMessageConverter producerJackson2MessageConverter(ObjectMapper jsonObjectMapper){
		return new Jackson2JsonMessageConverter(jsonObjectMapper);
	}
}
