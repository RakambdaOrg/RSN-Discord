package fr.raksrinana.rsndiscord.runner.impl;

import fr.raksrinana.rsndiscord.api.discordstatus.DiscordStatusApi;
import fr.raksrinana.rsndiscord.api.discordstatus.data.unresolvedincidents.Incident;
import fr.raksrinana.rsndiscord.api.discordstatus.data.unresolvedincidents.IncidentUpdate;
import fr.raksrinana.rsndiscord.runner.api.IScheduledRunner;
import fr.raksrinana.rsndiscord.runner.api.ScheduledRunner;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.impl.GuildConfiguration;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.SelfUser;
import org.jetbrains.annotations.NotNull;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import static fr.raksrinana.rsndiscord.utils.Utilities.DATE_TIME_MINUTE_FORMATTER;
import static java.util.Comparator.comparing;
import static java.util.concurrent.TimeUnit.MINUTES;

@ScheduledRunner
public class DiscordStatusRunner implements IScheduledRunner{
	private ZonedDateTime lastData;
	
	public DiscordStatusRunner(){
		lastData = ZonedDateTime.now();
	}
	
	@Override
	public void executeGlobal(@NotNull JDA jda){
		var selfUser = jda.getSelfUser();
		
		DiscordStatusApi.getIncidents().ifPresent(unresolvedIncidents -> {
			if(!unresolvedIncidents.getIncidents().isEmpty()){
				var embeds = unresolvedIncidents.getIncidents().stream()
						.filter(incident -> incident.getIncidentUpdates().stream()
								.anyMatch(update -> update.getUpdatedAt().isAfter(lastData)))
						.map(incident -> buildEmbed(selfUser, incident))
						.toList();
				
				if(!embeds.isEmpty()){
					jda.getGuilds().stream()
							.map(Settings::get)
							.map(GuildConfiguration::getDiscordIncidentsChannel)
							.flatMap(Optional::stream)
							.map(ChannelConfiguration::getChannel)
							.flatMap(Optional::stream)
							.forEach(channel -> embeds.forEach(embed -> JDAWrappers.message(channel, embed).submit()));
					
					lastData = unresolvedIncidents.getIncidents().stream()
							.map(Incident::getIncidentUpdates)
							.flatMap(Set::stream)
							.map(IncidentUpdate::getUpdatedAt)
							.max(ZonedDateTime::compareTo)
							.orElse(lastData);
				}
			}
		});
	}
	
	@Override
	public void executeGuild(@NotNull Guild guild){
	}
	
	@NotNull
	private MessageEmbed buildEmbed(@NotNull SelfUser selfUser, @NotNull Incident incident){
		var link = Optional.ofNullable(incident.getShortLink())
				.map(URL::toString)
				.orElse(null);
		
		var builder = new EmbedBuilder()
				.setAuthor(selfUser.getName(), link, selfUser.getAvatarUrl())
				.setColor(incident.getImpact().getColor())
				.setTitle("Discord incident")
				.setDescription(incident.getName())
				.addField("Created at", incident.getCreatedAt().format(DATE_TIME_MINUTE_FORMATTER), true)
				.addField("Impact", incident.getImpact().name(), true)
				.setFooter(incident.getId())
				.setTimestamp(incident.getUpdatedAt());
		
		var counter = new AtomicInteger(0);
		incident.getIncidentUpdates().stream()
				.sorted(comparing(IncidentUpdate::getUpdatedAt))
				.forEach(update -> {
					var title = "Update #" + counter.incrementAndGet();
					var message = update.getCreatedAt().format(DATE_TIME_MINUTE_FORMATTER) + " [" + update.getStatus().name() + "] : " + update.getBody();
					builder.addField(title, message, false);
				});
		
		return builder.build();
	}
	
	@Override
	public long getDelay(){
		return 5;
	}
	
	@NotNull
	@Override
	public String getName(){
		return "discordstatus";
	}
	
	@Override
	public long getPeriod(){
		return 10;
	}
	
	@NotNull
	@Override
	public TimeUnit getPeriodUnit(){
		return MINUTES;
	}
}
