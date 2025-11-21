package fr.rakambda.rsndiscord.spring.amqp;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.retry.RetryPolicy;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.util.backoff.ExponentialBackOff;
import tools.jackson.databind.json.JsonMapper;
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
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, JacksonJsonMessageConverter messageConverter){
		var backOff = new ExponentialBackOff();
		backOff.setInitialInterval(1000);
		backOff.setMultiplier(10);
		backOff.setMaxInterval(60000);
		
		var retryPolicy = RetryPolicy.builder()
				.backOff(backOff)
				.build();
		
		var retryTemplate = new RetryTemplate(retryPolicy);
		
		var rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setMessageConverter(messageConverter);
		rabbitTemplate.setRetryTemplate(retryTemplate);
		return rabbitTemplate;
	}
	
	@Bean
	public JacksonJsonMessageConverter producerJackson2MessageConverter(JsonMapper jsonMapper){
		return new JacksonJsonMessageConverter(jsonMapper);
	}
}
