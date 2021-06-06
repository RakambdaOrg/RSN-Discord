package fr.raksrinana.rsndiscord.runner.api;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.TimeUnit;

public interface IScheduledRunner{
	void executeGlobal(@NotNull JDA jda) throws Exception;
	
	void executeGuild(@NotNull Guild guild) throws Exception;
	
	long getDelay();
	
	@NotNull String getName();
	
	long getPeriod();
	
	@NotNull TimeUnit getPeriodUnit();
}
