package fr.rakambda.rsndiscord.spring.amqp;

import fr.rakambda.rsndiscord.spring.amqp.listeners.DelayedMessageHandler;
import fr.rakambda.rsndiscord.spring.amqp.message.DeleteMessageDelayMessage;
import fr.rakambda.rsndiscord.spring.amqp.message.IDelayedMessage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@Slf4j
public class QuartzService{
	private final Scheduler scheduler;
	private final JsonMapper jsonMapper;
	
	@Autowired
	public QuartzService(Scheduler scheduler, JsonMapper jsonMapper){
		this.scheduler = scheduler;
		this.jsonMapper = jsonMapper;
	}
	
	public void scheduleMessage(@NonNull Duration delay, @NonNull DeleteMessageDelayMessage message){
		scheduleMessage(delay, 5, message);
	}
	
	public void scheduleMessage(@NonNull Duration delay, int retryCount, @NonNull DeleteMessageDelayMessage message){
		scheduleMessage(delay, retryCount, DelayedMessageHandler.class, message);
	}
	
	@SneakyThrows(SchedulerException.class)
	public void scheduleMessage(@NonNull Duration delay, int retryCount, @NonNull Class<? extends Job> jobType, @NonNull IDelayedMessage message){
		log.info("Delaying by {} message {}", delay, message);
		var payloadJson = jsonMapper.writeValueAsString(message);
		var job = JobBuilder.newJob(jobType)
				.withIdentity(UUID.randomUUID().toString())
				.usingJobData("payload", payloadJson)
				.usingJobData("retry", retryCount)
				.build();
		
		var trigger = TriggerBuilder.newTrigger()
				.startAt(Instant.now().plus(delay))
				.build();
		
		scheduler.scheduleJob(job, trigger);
	}
}
