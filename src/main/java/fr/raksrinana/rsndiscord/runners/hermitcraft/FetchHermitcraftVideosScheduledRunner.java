package fr.raksrinana.rsndiscord.runners.hermitcraft;

import fr.raksrinana.rsndiscord.runners.ScheduledRunner;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.hermitcraft.HermitcraftUtils;
import fr.raksrinana.rsndiscord.utils.hermitcraft.data.HermitcraftVideo;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import java.awt.Color;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

public class FetchHermitcraftVideosScheduledRunner implements ScheduledRunner{
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z");
	private final JDA jda;
	
	public FetchHermitcraftVideosScheduledRunner(@NonNull JDA jda){
		this.jda = jda;
	}
	
	@Override
	public long getDelay(){
		return 2;
	}
	
	@Override
	public long getPeriod(){
		return 30;
	}
	
	@NonNull
	@Override
	public TimeUnit getPeriodUnit(){
		return TimeUnit.MINUTES;
	}
	
	@Override
	public void execute(){
		HermitcraftUtils.getVideos().ifPresent(videos -> this.jda.getGuilds().forEach(guild -> {
			final var config = Settings.get(guild).getHermitcraftConfiguration();
			config.getVideoNotificationChannel().flatMap(ChannelConfiguration::getChannel).ifPresent(channel -> videos.stream().sorted(Comparator.comparing(HermitcraftVideo::getUploaded)).filter(video -> !config.isVideoNotified(video.getId())).forEach(video -> {
				sendVideo(video, channel);
				config.setVideoNotified(video.getId());
			}));
		}));
	}
	
	private void sendVideo(HermitcraftVideo video, TextChannel channel){
		EmbedBuilder embed = Utilities.buildEmbed(this.jda.getSelfUser(), Color.GREEN, translate(channel.getGuild(), "runners.hermitcraft.uploaded", video.getUploader().getDisplayName()), "https://youtu.be/" + video.getId());
		embed.setDescription(video.getTitle());
		embed.addField(translate(channel.getGuild(), "runners.hermitcraft.uploader"), video.getUploader().getDisplayName(), true);
		embed.addField(translate(channel.getGuild(), "runners.hermitcraft.upload-date"), video.getUploaded().format(DF), true);
		embed.addField(translate(channel.getGuild(), "runners.hermitcraft.duration"), video.getFriendlyDuration(), true);
		embed.setFooter(video.getId());
		embed.setThumbnail(video.getUploader().getProfilePicture().toString());
		embed.setImage(String.format("https://i.ytimg.com/vi/%s/mqdefault.jpg", video.getId()));
		Actions.sendMessage(channel, "", embed.build());
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Hermitcraft video fetcher";
	}
}
