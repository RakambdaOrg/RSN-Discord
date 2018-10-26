package fr.mrcraftcod.gunterdiscord.runners;

import fr.mrcraftcod.gunterdiscord.settings.configs.MembersParticipationChannelConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.MembersParticipationConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.core.JDA;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
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
	private final boolean deleteStats;
	private final LocalDate date;
	
	/**
	 * Constructor.
	 *
	 * @param jda The JDA object.
	 */
	public DisplayDailyStatsScheduledRunner(final JDA jda){
		this(jda, LocalDate.now().minusDays(1), true);
	}
	
	/**
	 * Constructor.
	 *
	 * @param jda         The JDA object.
	 * @param date        The date to fetch the stats for.
	 * @param deleteStats Either to delete the stats after printing or not.
	 */
	public DisplayDailyStatsScheduledRunner(final JDA jda, final LocalDate date, final boolean deleteStats){
		getLogger(null).info("Creating daily stats runner");
		this.jda = jda;
		this.date = date;
		this.deleteStats = deleteStats;
	}
	
	@Override
	public void run(){
		getLogger(null).info("Starting daily stats runner");
		final var ytdKey = this.date.format(DF);
		final var date = this.date.format(DFD);
		for(final var guild : this.jda.getGuilds()){
			final var reportChannel = new MembersParticipationChannelConfig(guild).getObject(null);
			if(Objects.nonNull(reportChannel)){
				final var participationConfig = new MembersParticipationConfig(guild);
				final var stats = new MembersParticipationConfig(guild).getValue(ytdKey);
				if(Objects.nonNull(stats)){
					final var i = new AtomicInteger(0);
					getLogger(guild).debug("Processing stats for guild {}", guild);
					final var builder = Utilities.buildEmbed(this.jda.getSelfUser(), Color.MAGENTA, "Participation of the " + date);
					stats.entrySet().stream().sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())).map(e -> {
						final var user = this.jda.getUserById(e.getKey());
						if(Objects.nonNull(user)){
							return Map.entry(user, e.getValue());
						}
						return null;
					}).filter(Objects::nonNull).limit(10).forEachOrdered(e -> builder.addField("#" + i.getAndIncrement(), e.getKey().getAsMention() + " Messages: " + e.getValue(), false));
					Actions.sendMessage(reportChannel, builder.build());
					if(deleteStats){
						getLogger(guild).debug("Deleting stats of the day");
						participationConfig.deleteKey(ytdKey);
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
