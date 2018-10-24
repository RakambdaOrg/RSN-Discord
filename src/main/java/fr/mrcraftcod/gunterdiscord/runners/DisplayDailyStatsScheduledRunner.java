package fr.mrcraftcod.gunterdiscord.runners;

import fr.mrcraftcod.gunterdiscord.settings.configs.MembersParticipationChannelConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.MembersParticipationConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.core.JDA;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 14/07/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-07-14
 */
public class DisplayDailyStatsScheduledRunner implements ScheduledRunner{
	private final JDA jda;
	private final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyyMMdd");
	private final DateTimeFormatter DFD = DateTimeFormatter.ofPattern("dd/MM/yyy");
	
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
		getLogger(null).info("Starting roles runner");
		final var ytd = LocalDate.now().minusDays(1);
		final var ytdKey = ytd.format(DF);
		final var date = ytd.format(DFD);
		for(final var guild : this.jda.getGuilds()){
			final var reportChannel = new MembersParticipationChannelConfig(guild).getObject(null);
			if(Objects.nonNull(reportChannel)){
				final var participationConfig = new MembersParticipationConfig(guild);
				final var stats = new MembersParticipationConfig(guild).getValue(ytdKey);
				if(Objects.nonNull(stats)){
					final var builder = Utilities.buildEmbed(this.jda.getSelfUser(), Color.MAGENTA, "Participation of the " + date);
					stats.entrySet().stream().sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())).limit(10).forEachOrdered(e -> {
						builder.addField(this.jda.getUserById(e.getKey()).getAsMention(), "Messages: " + e.getValue(), false);
					});
					Actions.sendMessage(reportChannel, builder.build());
					participationConfig.deleteKey(ytdKey);
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
