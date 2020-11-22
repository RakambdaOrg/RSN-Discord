package fr.raksrinana.rsndiscord.modules.hermitcraft.runner;

import fr.raksrinana.rsndiscord.modules.hermitcraft.HermitcraftUtils;
import fr.raksrinana.rsndiscord.modules.hermitcraft.config.HermitcraftConfiguration;
import fr.raksrinana.rsndiscord.modules.hermitcraft.data.HermitcraftVideo;
import fr.raksrinana.rsndiscord.modules.settings.GuildConfiguration;
import fr.raksrinana.rsndiscord.modules.settings.Settings;
import fr.raksrinana.rsndiscord.modules.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.runner.IScheduledRunner;
import fr.raksrinana.rsndiscord.runner.ScheduledRunner;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import java.awt.Color;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.util.Comparator.comparing;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.stream.Collectors.toList;

@ScheduledRunner
public class VideosRunner implements IScheduledRunner{
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z");
	private final JDA jda;
	
	public VideosRunner(@NonNull JDA jda){
		this.jda = jda;
	}
	
	@Override
	public long getDelay(){
		return 2;
	}
	
	@Override
	public long getPeriod(){
		return 20;
	}
	
	@Override
	public void execute(){
		var hermitcraftGeneral = Settings.getGeneral().getHermitcraft();
		var channels = jda.getGuilds().stream()
				.map(Settings::get)
				.map(GuildConfiguration::getHermitcraftConfiguration)
				.map(HermitcraftConfiguration::getVideoNotificationChannel)
				.flatMap(Optional::stream)
				.map(ChannelConfiguration::getChannel)
				.flatMap(Optional::stream)
				.collect(toList());
		
		HermitcraftUtils.getVideos().stream()
				.flatMap(List::stream)
				.filter(video -> !hermitcraftGeneral.isVideoNotified(video.getId()))
				.sorted(comparing(HermitcraftVideo::getUploaded))
				.forEach(video -> {
					channels.forEach(channel -> sendVideo(video, channel));
					hermitcraftGeneral.setVideoNotified(video.getId());
				});
	}
	
	@NonNull
	@Override
	public TimeUnit getPeriodUnit(){
		return MINUTES;
	}
	
	private void sendVideo(HermitcraftVideo video, TextChannel channel){
		var selfUser = this.jda.getSelfUser();
		var guild = channel.getGuild();
		
		var embed = new EmbedBuilder().setAuthor(selfUser.getName(), null, selfUser.getAvatarUrl())
				.setColor(Color.GREEN)
				.setTitle(translate(guild, "hermitcraft.uploaded", video.getUploader().getDisplayName()), "https://youtu.be/" + video.getId())
				.setDescription(video.getTitle())
				.addField(translate(guild, "hermitcraft.uploader"), video.getUploader().getDisplayName(), true)
				.addField(translate(guild, "hermitcraft.upload-date"), video.getUploaded().format(DF), true)
				.addField(translate(guild, "hermitcraft.duration"), video.getFriendlyDuration(), true)
				.setImage(String.format("https://i.ytimg.com/vi/%s/mqdefault.jpg", video.getId()))
				.setThumbnail(video.getUploader().getProfilePicture().toString())
				.setFooter(video.getId())
				.build();
		channel.sendMessage(embed).submit();
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Hermitcraft video fetcher";
	}
}
