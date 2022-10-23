package fr.rakambda.rsndiscord.spring.schedule.impl.hermitcraft;

import fr.rakambda.rsndiscord.spring.api.exceptions.RequestFailedException;
import fr.rakambda.rsndiscord.spring.api.hermitcraft.HermitcraftService;
import fr.rakambda.rsndiscord.spring.api.hermitcraft.data.HermitcraftVideo;
import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import fr.rakambda.rsndiscord.spring.schedule.WrappedTriggerTask;
import fr.rakambda.rsndiscord.spring.storage.entity.ChannelType;
import fr.rakambda.rsndiscord.spring.storage.entity.HermitcraftVideoEntity;
import fr.rakambda.rsndiscord.spring.storage.repository.ChannelRepository;
import fr.rakambda.rsndiscord.spring.storage.repository.HermitcraftVideoRepository;
import fr.rakambda.rsndiscord.spring.util.LocalizationService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.awt.Color;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Collection;
import java.util.Optional;
import static java.util.Comparator.comparing;

@Component
@Slf4j
public class VideoRunner extends WrappedTriggerTask{
	private final ChannelRepository channelRepository;
	private final HermitcraftService hermitcraftService;
	private final HermitcraftVideoRepository hermitcraftVideoRepository;
	private final LocalizationService localizationService;
	
	@Autowired
	public VideoRunner(@NotNull JDA jda, ChannelRepository channelRepository, HermitcraftService hermitcraftService, HermitcraftVideoRepository hermitcraftVideoRepository, LocalizationService localizationService){
		super(jda);
		this.channelRepository = channelRepository;
		this.hermitcraftService = hermitcraftService;
		this.hermitcraftVideoRepository = hermitcraftVideoRepository;
		this.localizationService = localizationService;
	}
	
	@Override
	@NotNull
	public String getId(){
		return "hermitcraft.video";
	}
	
	@NotNull
	@Override
	protected String getName(){
		return "Hermitcraft video fetcher";
	}
	
	@Override
	protected long getPeriod(){
		return 20;
	}
	
	@NotNull
	@Override
	public TemporalUnit getPeriodUnit(){
		return ChronoUnit.MINUTES;
	}
	
	@Override
	protected void executeGlobal(@NotNull JDA jda) throws RequestFailedException{
		var videos = hermitcraftService.getVideos();
		var channels = getChannels(jda);
		
		if(channels.isEmpty()){
			log.info("No channels configured, skipping");
			return;
		}
		
		videos.stream()
				.filter(video -> !isVideoNotified(video.getId()))
				.sorted(comparing(HermitcraftVideo::getUploaded))
				.peek(video -> sendVideo(video, channels))
				.map(HermitcraftVideo::getId)
				.map(i -> new HermitcraftVideoEntity(i, Instant.now()))
				.forEach(hermitcraftVideoRepository::save);
	}
	
	private boolean isVideoNotified(@NotNull String videoId){
		return hermitcraftVideoRepository.existsById(videoId);
	}
	
	@NotNull
	private Collection<GuildMessageChannel> getChannels(@NotNull JDA jda){
		return channelRepository.findAllByType(ChannelType.HERMITCRAFT_VIDEO).stream()
				.map(entity -> JDAWrappers.findChannel(jda, entity.getChannelId()))
				.flatMap(Optional::stream)
				.toList();
	}
	
	@Override
	protected void executeGuild(@NotNull Guild guild){
	}
	
	private void sendVideo(@NotNull HermitcraftVideo video, @NotNull Collection<GuildMessageChannel> channels){
		channels.forEach(c -> sendVideo(video, c));
	}
	
	private void sendVideo(@NotNull HermitcraftVideo video, @NotNull GuildMessageChannel channel){
		var guild = channel.getGuild();
		var locale = guild.getLocale();
		
		var embed = new EmbedBuilder()
				.setColor(Color.GREEN)
				.setTitle(localizationService.translate(locale, "hermitcraft.uploaded", video.getUploader().getDisplayName()), "https://youtu.be/" + video.getId())
				.setDescription(video.getTitle())
				.addField(localizationService.translate(locale, "hermitcraft.uploader"), video.getUploader().getDisplayName(), true)
				.addField(localizationService.translate(locale, "hermitcraft.duration"), video.getFriendlyDuration(), true)
				.setImage(String.format("https://i.ytimg.com/vi/%s/mqdefault.jpg", video.getId()))
				.setThumbnail(video.getUploader().getProfilePicture().toString())
				.setFooter(video.getId())
				.setTimestamp(video.getUploaded())
				.build();
		JDAWrappers.message(channel, embed).submit();
	}
}
