package fr.rakambda.rsndiscord.spring.schedule;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.task.TaskSchedulerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.TriggerTask;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
@EnableScheduling
@Slf4j
public class ScheduleConfiguration implements SchedulingConfigurer{
	private final List<TriggerTaskProvider> triggers;
	
	@Autowired
	public ScheduleConfiguration(List<TriggerTaskProvider> triggers){
		this.triggers = triggers;
	}
	
	@Override
	public void configureTasks(@NotNull ScheduledTaskRegistrar taskRegistrar){
		taskRegistrar.setScheduler(taskExecutor());
		
		triggers.forEach(trigger -> addTriggerTask(taskRegistrar, trigger));
	}
	
	private void addTriggerTask(ScheduledTaskRegistrar taskRegistrar, TriggerTaskProvider triggerTaskProvider){
		log.info("Registering scheduled task {}", triggerTaskProvider);
		taskRegistrar.addTriggerTask(new TriggerTask(triggerTaskProvider, triggerTaskProvider));
	}
	
	@Bean
	public ThreadPoolTaskScheduler taskScheduler(TaskSchedulerBuilder builder) {
		return builder.build();
	}
	
	@Bean(destroyMethod = "shutdown")
	public ScheduledExecutorService taskExecutor(){
		return Executors.newScheduledThreadPool(1);
	}
}
