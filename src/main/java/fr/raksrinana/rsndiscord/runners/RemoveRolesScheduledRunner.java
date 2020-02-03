package fr.raksrinana.rsndiscord.runners;

import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.log.Log;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.JDA;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class RemoveRolesScheduledRunner implements ScheduledRunner{
	@Getter
	private final JDA jda;
	
	public RemoveRolesScheduledRunner(JDA jda){
		this.jda = jda;
	}
	
	@Override
	public void execute(){
		final var currentDate = LocalDateTime.now();
		for(final var guild : this.getJda().getGuilds()){
			Log.getLogger(guild).debug("Processing guild {}", guild);
			final var it = Settings.get(guild).getRemoveRoles().iterator();
			while(it.hasNext()){
				final var ban = it.next();
				if(currentDate.isAfter(ban.getDate())){
					ban.getUser().getUser().ifPresent(user -> ban.getRole().getRole().ifPresentOrElse(role -> {
						Optional.ofNullable(guild.getMember(user)).ifPresent(member -> Actions.removeRole(member, role));
						it.remove();
					}, it::remove));
				}
			}
		}
	}
	
	@NonNull
	@Override
	public String getName(){
		return "roles remover";
	}
	
	@Override
	public long getDelay(){
		return 1;
	}
	
	@Override
	public long getPeriod(){
		return 15;
	}
	
	@NonNull
	@Override
	public TimeUnit getPeriodUnit(){
		return TimeUnit.MINUTES;
	}
}
