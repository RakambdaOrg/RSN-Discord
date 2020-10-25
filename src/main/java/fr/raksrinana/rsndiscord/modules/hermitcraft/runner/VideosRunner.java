package fr.raksrinana.rsndiscord.modules.hermitcraft.runner;

import fr.raksrinana.rsndiscord.modules.hermitcraft.HermitcraftUtils;
import fr.raksrinana.rsndiscord.modules.hermitcraft.config.HermitcraftConfiguration;
import fr.raksrinana.rsndiscord.modules.hermitcraft.data.HermitcraftVideo;
import fr.raksrinana.rsndiscord.modules.settings.GuildConfiguration;
import fr.raksrinana.rsndiscord.modules.settings.Settings;
import fr.raksrinana.rsndiscord.modules.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.runner.IScheduledRunner;
import fr.raksrinana.rsndiscord.runner.ScheduledRunner;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.Utilities;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import java.awt.Color;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

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
	
	@NonNull
	@Override
	public TimeUnit getPeriodUnit(){
		return TimeUnit.MINUTES;
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
				.collect(Collectors.toList());
		
		HermitcraftUtils.getVideos().stream()
				.flatMap(List::stream)
				.filter(video -> !hermitcraftGeneral.isVideoNotified(video.getId()))
				.sorted(Comparator.comparing(HermitcraftVideo::getUploaded))
				.forEach(video -> {
					channels.forEach(channel -> sendVideo(video, channel));
					hermitcraftGeneral.setVideoNotified(video.getId());
				});
	}
	
	private void sendVideo(HermitcraftVideo video, TextChannel channel){
		EmbedBuilder embed = Utilities.buildEmbed(this.jda.getSelfUser(), Color.GREEN, translate(channel.getGuild(), "hermitcraft.uploaded", video.getUploader().getDisplayName()), "https://youtu.be/" + video.getId());
		embed.setDescription(video.getTitle());
		embed.addField(translate(channel.getGuild(), "hermitcraft.uploader"), video.getUploader().getDisplayName(), true);
		embed.addField(translate(channel.getGuild(), "hermitcraft.upload-date"), video.getUploaded().format(DF), true);
		embed.addField(translate(channel.getGuild(), "hermitcraft.duration"), video.getFriendlyDuration(), true);
		embed.setFooter(video.getId());
		embed.setThumbnail(video.getUploader().getProfilePicture().toString());
		embed.setImage(String.format("https://i.ytimg.com/vi/%s/mqdefault.jpg", video.getId()));
		Actions.sendEmbed(channel, embed.build());
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Hermitcraft video fetcher";
	}
}
