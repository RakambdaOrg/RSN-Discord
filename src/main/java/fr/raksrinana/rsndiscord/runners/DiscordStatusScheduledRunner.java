package fr.raksrinana.rsndiscord.runners;

import fr.raksrinana.rsndiscord.settings.GuildConfiguration;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.discordstatus.DiscordStatus;
import fr.raksrinana.rsndiscord.utils.discordstatus.data.unresolvedincidents.Incident;
import fr.raksrinana.rsndiscord.utils.discordstatus.data.unresolvedincidents.IncidentUpdate;
import lombok.NonNull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class DiscordStatusScheduledRunner implements ScheduledRunner{
	private final JDA jda;
	private ZonedDateTime lastData;
	
	public DiscordStatusScheduledRunner(JDA jda){
		this.jda = jda;
		this.lastData = ZonedDateTime.now();
	}
	
	@Override
	public void execute(){
		DiscordStatus.getIncidents().ifPresent(unresolvedIncidents -> {
			if(!unresolvedIncidents.getIncidents().isEmpty()){
				var embeds = unresolvedIncidents.getIncidents().stream()
						.filter(incident -> incident.getIncidentUpdates().stream()
								.anyMatch(update -> update.getUpdatedAt().isAfter(lastData)))
						.map(this::buildEmbed)
						.collect(Collectors.toList());
				if(!embeds.isEmpty()){
					this.jda.getGuilds().stream().map(Settings::get)
							.map(GuildConfiguration::getDiscordIncidentsChannel)
							.flatMap(Optional::stream)
							.map(ChannelConfiguration::getChannel)
							.flatMap(Optional::stream)
							.forEach(channel -> embeds.forEach(embed -> Actions.sendEmbed(channel, embed)));
					
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
	
	private MessageEmbed buildEmbed(Incident incident){
		var builder = Utilities.buildEmbed(this.jda.getSelfUser(), incident.getImpact().getColor(), "Discord incident", null);
		builder.setDescription(incident.getName());
		builder.setFooter(incident.getId());
		builder.setTimestamp(incident.getUpdatedAt());
		
		builder.addField("Created at", incident.getCreatedAt().format(Utilities.DATE_TIME_MINUTE_FORMATTER), true);
		builder.addField("Impact", incident.getImpact().name(), true);
		
		var counter = new AtomicInteger(0);
		incident.getIncidentUpdates().stream()
				.sorted(Comparator.comparing(IncidentUpdate::getUpdatedAt))
				.forEach(update -> {
					builder.addField("Update #" + counter.incrementAndGet(),
							update.getCreatedAt().format(Utilities.DATE_TIME_MINUTE_FORMATTER) + " [" + update.getStatus().name() + "] : " + update.getBody(),
							false);
				});
		
		return builder.build();
	}
	
	@Override
	public long getDelay(){
		return 5;
	}
	
	@NonNull
	@Override
	public String getName(){
		return "discordstatus";
	}
	
	@Override
	public long getPeriod(){
		return 10;
	}
	
	@NonNull
	@Override
	public TimeUnit getPeriodUnit(){
		return TimeUnit.MINUTES;
	}
}
