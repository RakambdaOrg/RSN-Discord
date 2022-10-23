package fr.rakambda.rsndiscord.spring.amqp;

import fr.rakambda.rsndiscord.spring.settings.ApplicationSettings;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Getter
public class AmqpNameProvider{
	private final String exchangeDelayName;
	private final String queueDelayName;
	private final String routingKeyDelay;
	private final String xRedeliverCountRemainingHeader;
	
	@Autowired
	public AmqpNameProvider(ApplicationSettings applicationSettings){
		var prefix = applicationSettings.getAmqpPrefix();
		
		exchangeDelayName = "%s.exchange.delay".formatted(prefix);
		queueDelayName = "%s.queue.delay".formatted(prefix);
		routingKeyDelay = "schedule-routing-key";
		xRedeliverCountRemainingHeader = "x-redeliver-count-remaining";
	}
}
