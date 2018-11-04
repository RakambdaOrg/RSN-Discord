package fr.mrcraftcod.gunterdiscord.runners;

import fr.mrcraftcod.gunterdiscord.commands.EmotesCommand;
import fr.mrcraftcod.gunterdiscord.commands.TempParticipationCommand;
import fr.mrcraftcod.gunterdiscord.settings.configs.*;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.User;
import java.time.LocalDate;
import java.util.Objects;
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
	public DisplayDailyStatsScheduledRunner(final JDA jda){
		getLogger(null).info("Creating daily stats runner");
		this.jda = jda;
	}
	
	@Override
	public void run(){
		getLogger(null).info("Starting daily stats runner");
		final var ytd = LocalDate.now().minusDays(1);
		final var lastWeek = LocalDate.now().minusWeeks(1);
		for(final var guild : this.jda.getGuilds()){
			final var membersParticipationChannel = new MembersParticipationChannelConfig(guild).getObject(null);
			if(Objects.nonNull(membersParticipationChannel)){
				getLogger(guild).debug("Processing stats for guild {}", guild);
				if(TempParticipationCommand.sendInfos(guild, ytd, jda.getSelfUser(), membersParticipationChannel)){
					new MembersParticipationConfig(guild).deleteKey(TempParticipationCommand.getKey(ytd));
					final var usersToPin = new MembersParticipationPinConfig(guild).getAsList();
					if(!usersToPin.isEmpty()){
						Actions.sendMessage(membersParticipationChannel, usersToPin.stream().map(User::getAsMention).collect(Collectors.joining("\n")));
					}
				}
			}
			
			final var emotesParticipationChannel = new EmotesParticipationChannelConfig(guild).getObject(null);
			if(Objects.nonNull(emotesParticipationChannel)){
				getLogger(guild).debug("Processing stats for guild {}", guild);
				if(EmotesCommand.sendInfos(guild, lastWeek, jda.getSelfUser(), emotesParticipationChannel)){
					new EmotesParticipationConfig(guild).deleteKey(EmotesCommand.getKey(lastWeek));
					final var usersToPin = new EmotesParticipationPinConfig(guild).getAsList();
					if(!usersToPin.isEmpty()){
						Actions.sendMessage(emotesParticipationChannel, usersToPin.stream().map(User::getAsMention).collect(Collectors.joining("\n")));
					}
				}
			}
		}
		getLogger(null).info("Daily stats runner done");
	}
	
	@Override
	public long getPeriod(){
		return 2;
	}
	
	@Override
	public TimeUnit getPeriodUnit(){
		return TimeUnit.HOURS;
	}
	
	@Override
	public long getDelay(){
		return 0;
	}
}
