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
	private final String queueDelayStagingName;
	private final String routingKeyDelay;
	private final String routingKeyDelayStaging;
	private final String xRedeliverCountRemainingHeader;
	
	@Autowired
	public AmqpNameProvider(ApplicationSettings applicationSettings){
		var prefix = applicationSettings.getAmqpPrefix();
		
		exchangeDelayName = "%s.exchange.delay".formatted(prefix);
		queueDelayName = "%s.queue.delay".formatted(prefix);
		queueDelayStagingName = "%s.queue.delay.staging".formatted(prefix);
		routingKeyDelay = "schedule-routing-key";
		routingKeyDelayStaging = "schedule-routing-key-staging";
		xRedeliverCountRemainingHeader = "x-redeliver-count-remaining";
	}
}
