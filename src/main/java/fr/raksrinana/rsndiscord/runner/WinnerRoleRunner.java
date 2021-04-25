package fr.raksrinana.rsndiscord.runner;

import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.TimeUnit;
import static java.util.concurrent.TimeUnit.HOURS;

@ScheduledRunner
public class WinnerRoleRunner implements IScheduledRunner{
	private final JDA jda;
	
	public WinnerRoleRunner(@NotNull JDA jda){
		this.jda = jda;
	}
	
	@Override
	public void execute(){
		jda.getGuilds().forEach(guild -> {
			Log.getLogger(guild).info("Processing guild {}", guild);
			var eventConfiguration = Settings.get(guild).getEventConfiguration();
			eventConfiguration.getWinnerRole()
					.flatMap(RoleConfiguration::getRole)
					.ifPresent(role -> guild.findMembersWithRoles(role)
							.onSuccess(members -> members.stream()
									.filter(eventConfiguration::isRoleTimeOver)
									.forEach(member -> JDAWrappers.removeRole(member, role))));
		});
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
