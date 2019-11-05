package fr.raksrinana.rsndiscord.runners;

import fr.raksrinana.rsndiscord.settings.NewSettings;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.log.Log;
import net.dv8tion.jda.api.JDA;
import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 14/07/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-07-14
 */
public class RemoveRolesScheduledRunner implements ScheduledRunner{
	private final JDA jda;
	
	/**
	 * Constructor.
	 *
	 * @param jda The JDA object.
	 */
	public RemoveRolesScheduledRunner(@Nonnull final JDA jda){
		Log.getLogger(null).info("Creating roles runner");
		this.jda = jda;
	}
	
	@Override
	public void run(){
		Log.getLogger(null).info("Starting roles runner");
		final var currentDate = LocalDateTime.now();
		for(final var guild : this.jda.getGuilds()){
			Log.getLogger(guild).debug("Processing guild {}", guild);
			final var it = NewSettings.getConfiguration(guild).getRemoveRoles().iterator();
			while(it.hasNext()){
				final var ban = it.next();
				if(currentDate.isAfter(ban.getEndDate())){
					ban.getUser().getUser().ifPresent(user -> ban.getRole().getRole().ifPresentOrElse(role -> {
						Log.getLogger(guild).debug("Removed role {} for user {}", role, user);
						Actions.removeRole(user, role);
						it.remove();
					}, it::remove));
				}
			}
		}
		Log.getLogger(null).info("Roles runner done");
	}
	
	@Override
	public long getPeriod(){
		return 15;
	}
	
	@Nonnull
	@Override
	public TimeUnit getPeriodUnit(){
		return TimeUnit.MINUTES;
	}
	
	@Override
	public long getDelay(){
		return 1;
	}
}
