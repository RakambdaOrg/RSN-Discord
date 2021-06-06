package fr.raksrinana.rsndiscord.runner.impl;

import fr.raksrinana.rsndiscord.runner.api.IScheduledRunner;
import fr.raksrinana.rsndiscord.runner.api.ScheduledRunner;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.TimeUnit;
import static java.util.concurrent.TimeUnit.HOURS;

@ScheduledRunner
@Log4j2
public class WinnerRoleRunner implements IScheduledRunner{
	@Override
	public void executeGlobal(@NotNull JDA jda){
	}
	
	@Override
	public void executeGuild(@NotNull Guild guild) throws Exception{
		log.info("Processing guild {}", guild);
		var eventConfiguration = Settings.get(guild).getEventConfiguration();
		eventConfiguration.getWinnerRole()
				.flatMap(RoleConfiguration::getRole)
				.ifPresent(role -> guild.findMembersWithRoles(role)
						.onSuccess(members -> members.stream()
								.filter(eventConfiguration::isRoleTimeOver)
								.forEach(member -> JDAWrappers.removeRole(member, role))));
	}
	
	@Override
	public long getDelay(){
		return 0;
	}
	
	@NotNull
	@Override
	public String getName(){
		return "winnerRole";
	}
	
	@Override
	public long getPeriod(){
		return 1;
	}
	
	@NotNull
	@Override
	public TimeUnit getPeriodUnit(){
		return HOURS;
	}
}
