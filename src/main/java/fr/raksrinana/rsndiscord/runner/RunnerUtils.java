package fr.raksrinana.rsndiscord.runner;

import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.utils.Utilities;
import net.dv8tion.jda.api.JDA;

public class RunnerUtils{
	public static void registerAllScheduledRunners(){
		Utilities.getAllAnnotatedWith(ScheduledRunner.class, clazz -> (IScheduledRunner) clazz.getConstructor(JDA.class).newInstance(Main.getJda()))
				.peek(c -> Log.getLogger(null).info("Loaded scheduled runner {}", c.getClass().getName()))
				.forEach(scheduledRunner -> Main.getExecutorService().scheduleAtFixedRate(
						new ScheduledRunnerWrapper(scheduledRunner),
						scheduledRunner.getDelay(),
						scheduledRunner.getPeriod(),
						scheduledRunner.getPeriodUnit()
				));
	}
}