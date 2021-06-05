package fr.raksrinana.rsndiscord.runner;

import fr.raksrinana.rsndiscord.Main;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDA;
import static fr.raksrinana.rsndiscord.utils.Utilities.getAllAnnotatedWith;

@Log4j2
public class RunnerUtils{
	public static void registerAllScheduledRunners(){
		log.info("Creating schedule runners");
		getAllAnnotatedWith(ScheduledRunner.class, clazz -> (IScheduledRunner) clazz.getConstructor(JDA.class).newInstance(Main.getJda()))
				.peek(c -> log.info("Loaded scheduled runner {}", c.getClass().getName()))
				.forEach(scheduledRunner -> Main.getExecutorService().scheduleAtFixedRate(
						new ScheduledRunnerWrapper(scheduledRunner),
						scheduledRunner.getDelay(),
						scheduledRunner.getPeriod(),
						scheduledRunner.getPeriodUnit()
				));
	}
}
