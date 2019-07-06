package fr.mrcraftcod.gunterdiscord.runners;

import fr.mrcraftcod.gunterdiscord.settings.NewSettings;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.api.JDA;
import javax.annotation.Nonnull;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

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
		getLogger(null).info("Creating roles runner");
		this.jda = jda;
	}
	
	@Override
	public void run(){
		getLogger(null).info("Starting roles runner");
		final var currentDate = new Date();
		for(final var guild : this.jda.getGuilds()){
			getLogger(guild).debug("Processing guild {}", guild);
			NewSettings.getConfiguration(guild).getRemoveRoles().removeAll(NewSettings.getConfiguration(guild).getRemoveRoles().stream().filter(ban -> ban.getEndDate().after(currentDate)).peek(ban -> ban.getRole().ifPresent(role -> ban.getUser().ifPresent(user -> {
				getLogger(guild).debug("Removed role {} for user {}", role, user);
				Actions.removeRole(user, role);
			}))).collect(Collectors.toList()));
		}
		getLogger(null).info("Roles runner done");
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
