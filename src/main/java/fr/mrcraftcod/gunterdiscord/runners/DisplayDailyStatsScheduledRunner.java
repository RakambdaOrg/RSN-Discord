package fr.mrcraftcod.gunterdiscord.runners;

import fr.mrcraftcod.gunterdiscord.commands.EmotesCommand;
import fr.mrcraftcod.gunterdiscord.commands.TempParticipationCommand;
import fr.mrcraftcod.gunterdiscord.settings.configs.*;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 14/07/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-07-14
 */
public class DisplayDailyStatsScheduledRunner implements ScheduledRunner{
	private final JDA jda;
	
	/**
	 * Constructor.
	 *
	 * @param jda The JDA object.
	 */
	public DisplayDailyStatsScheduledRunner(@Nonnull final JDA jda){
		getLogger(null).info("Creating daily stats runner");
		this.jda = jda;
	}
	
	@Override
	public void run(){
		getLogger(null).info("Starting daily stats runner");
		final var ytd = LocalDate.now().minusDays(1);
		final var lastWeek = LocalDate.now().minusWeeks(1);
		for(final var guild : this.jda.getGuilds()){
			new MembersParticipationChannelConfig(guild).getObject().ifPresent(membersParticipationChannel -> {
				getLogger(guild).debug("Processing stats for guild {}", guild);
				if(TempParticipationCommand.sendInfos(guild, ytd, this.jda.getSelfUser(), membersParticipationChannel)){
					new MembersParticipationConfig(guild).deleteKey(TempParticipationCommand.getKey(ytd));
					new MembersParticipationPinConfig(guild).getAsList().ifPresent(usersToPin -> Actions.sendMessage(membersParticipationChannel, usersToPin.stream().map(User::getAsMention).collect(Collectors.joining("\n"))));
				}
			});
			new EmotesParticipationChannelConfig(guild).getObject().ifPresent(emotesParticipationChannel -> {
				getLogger(guild).debug("Processing stats for guild {}", guild);
				if(EmotesCommand.sendInfos(guild, lastWeek, this.jda.getSelfUser(), emotesParticipationChannel, 10)){
					new EmotesParticipationConfig(guild).deleteKey(EmotesCommand.getKey(lastWeek));
					new EmotesParticipationPinConfig(guild).getAsList().ifPresent(usersToPin -> Actions.sendMessage(emotesParticipationChannel, usersToPin.stream().map(User::getAsMention).collect(Collectors.joining("\n"))));
				}
			});
		}
		getLogger(null).info("Daily stats runner done");
	}
	
	@Override
	public long getPeriod(){
		return 2;
	}
	
	@Nonnull
	@Override
	public TimeUnit getPeriodUnit(){
		return TimeUnit.HOURS;
	}
	
	@Override
	public long getDelay(){
		return 0;
	}
}
