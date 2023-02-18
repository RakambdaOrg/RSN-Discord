package fr.rakambda.rsndiscord.spring.schedule.impl.hermitcraft;

import fr.rakambda.rsndiscord.spring.amqp.RabbitService;
import fr.rakambda.rsndiscord.spring.api.exceptions.RequestFailedException;
import fr.rakambda.rsndiscord.spring.api.hermitcraft.HermitcraftService;
import fr.rakambda.rsndiscord.spring.api.hermitcraft.data.Hermit;
import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import fr.rakambda.rsndiscord.spring.schedule.WrappedTriggerTask;
import fr.rakambda.rsndiscord.spring.storage.entity.ChannelType;
import fr.rakambda.rsndiscord.spring.storage.repository.ChannelRepository;
import fr.rakambda.rsndiscord.spring.util.LocalizationService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import java.awt.Color;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import static java.util.Optional.empty;

@Profile("hermitcraft-stream")
@Component
@Slf4j
public class StreamRunner extends WrappedTriggerTask{
	private final ChannelRepository channelRepository;
	private final HermitcraftService hermitcraftService;
	private final LocalizationService localizationService;
	private final RabbitService rabbitService;
	private final Set<String> previousLives;
	
	@Autowired
	public StreamRunner(@NotNull JDA jda, ChannelRepository channelRepository, HermitcraftService hermitcraftService, LocalizationService localizationService, RabbitService rabbitService){
		super(jda);
		this.channelRepository = channelRepository;
		this.hermitcraftService = hermitcraftService;
		this.localizationService = localizationService;
		this.rabbitService = rabbitService;
		
		previousLives = new HashSet<>();
	}
	
	@Override
	@NotNull
	public String getId(){
		return "hermitcraft.stream";
	}
	
	@NotNull
	@Override
	protected String getName(){
		return "Hermitcraft stream fetcher";
	}
	
	@Override
	protected long getPeriod(){
		return 5;
	}
	
	@NotNull
	@Override
	public TemporalUnit getPeriodUnit(){
		return ChronoUnit.MINUTES;
	}
	
	@Override
	protected void executeGlobal(@NotNull JDA jda) throws RequestFailedException{
		var hermits = hermitcraftService.getHermits();
		var channels = getChannels(jda);
		
		if(channels.isEmpty()){
			log.info("No channels configured, skipping");
			return;
		}
		
		var hermitsLive = hermits.stream().filter(Hermit::isLive).toList();
		
		var newLives = hermitsLive.stream().filter(l -> !previousLives.contains(l.getChannelId())).toList();
		previousLives.clear();
		previousLives.addAll(hermitsLive.stream().map(Hermit::getChannelId).toList());
		
		newLives.forEach(h -> sendStream(h, channels));
	}
	
	@NotNull
	private Collection<GuildMessageChannel> getChannels(@NotNull JDA jda){
		return channelRepository.findAllByType(ChannelType.HERMITCRAFT_LIVE).stream()
				.map(entity -> JDAWrappers.findChannel(jda, entity.getChannelId()))
				.flatMap(Optional::stream)
				.toList();
	}
	
	@Override
	protected void executeGuild(@NotNull Guild guild){
	}
	
	private void sendStream(@NotNull Hermit hermit, @NotNull Collection<GuildMessageChannel> channels){
		channels.forEach(c -> sendStream(hermit, c));
	}
	
	private void sendStream(@NotNull Hermit hermit, @NotNull GuildMessageChannel channel){
		var url = getLiveUrl(hermit).orElse(null);
		var locale = channel.getGuild().getLocale();
		
		var embed = new EmbedBuilder()
				.setColor(Color.GREEN)
				.setTitle(localizationService.translate(locale, "hermitcraft.live", hermit.getDisplayName()), url)
				.addField(localizationService.translate(locale, "hermitcraft.hermit"), hermit.getDisplayName(), true)
				.setThumbnail(hermit.getProfilePicture().toString())
				.build();
		JDAWrappers.message(channel, embed).submitAndDelete(Duration.ofHours(12), rabbitService);
	}
	
	@NotNull
	private Optional<String> getLiveUrl(@NotNull Hermit hermit){
		if(hermit.isStreaming()){
			return Optional.of("https://twitch.tv/" + hermit.getTwitchName());
		}
		if(hermit.isBeamStreaming()){
			return Optional.of("https://mixer.com/" + hermit.getBeamName());
		}
		if(hermit.isYtStreaming()){
			return Optional.of("https://youtube.com/channel/" + hermit.getChannelId());
		}
		return empty();
	}
}
