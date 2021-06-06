package fr.raksrinana.rsndiscord.runner.impl.hermitcraft;

import fr.raksrinana.rsndiscord.api.hermitcraft.HermitcraftApi;
import fr.raksrinana.rsndiscord.api.hermitcraft.data.HermitcraftVideo;
import fr.raksrinana.rsndiscord.runner.api.IScheduledRunner;
import fr.raksrinana.rsndiscord.runner.api.ScheduledRunner;
import fr.raksrinana.rsndiscord.settings.GuildConfiguration;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.HermitcraftConfiguration;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;
import java.awt.Color;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.util.Comparator.comparing;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.stream.Collectors.toList;

@ScheduledRunner
public class VideosRunner implements IScheduledRunner{
	@Override
	public long getDelay(){
		return 2;
	}
	
	@Override
	public long getPeriod(){
		return 20;
	}
	
	@Override
	public void executeGlobal(@NotNull JDA jda){
		var hermitcraftGeneral = Settings.getGeneral().getHermitcraft();
		var channels = jda.getGuilds().stream()
				.map(Settings::get)
				.map(GuildConfiguration::getHermitcraftConfiguration)
				.map(HermitcraftConfiguration::getVideoNotificationChannel)
				.flatMap(Optional::stream)
				.map(ChannelConfiguration::getChannel)
				.flatMap(Optional::stream)
				.collect(toList());
		
		HermitcraftApi.getVideos().stream()
				.flatMap(List::stream)
				.filter(video -> !hermitcraftGeneral.isVideoNotified(video.getId()))
				.sorted(comparing(HermitcraftVideo::getUploaded))
				.forEach(video -> {
					channels.forEach(channel -> sendVideo(jda.getSelfUser(), video, channel));
					hermitcraftGeneral.setVideoNotified(video.getId());
				});
	}
	
	@Override
	public void executeGuild(@NotNull Guild guild){
	}
	
	private void sendVideo(@NotNull SelfUser selfUser, @NotNull HermitcraftVideo video, @NotNull TextChannel channel){
		var guild = channel.getGuild();
		
		var embed = new EmbedBuilder().setAuthor(selfUser.getName(), null, selfUser.getAvatarUrl())
				.setColor(Color.GREEN)
				.setTitle(translate(guild, "hermitcraft.uploaded", video.getUploader().getDisplayName()), "https://youtu.be/" + video.getId())
				.setDescription(video.getTitle())
				.addField(translate(guild, "hermitcraft.uploader"), video.getUploader().getDisplayName(), true)
				.addField(translate(guild, "hermitcraft.duration"), video.getFriendlyDuration(), true)
				.setImage(String.format("https://i.ytimg.com/vi/%s/mqdefault.jpg", video.getId()))
				.setThumbnail(video.getUploader().getProfilePicture().toString())
				.setFooter(video.getId())
				.setTimestamp(video.getUploaded())
				.build();
		JDAWrappers.message(channel, embed).submit();
	}
	
	@NotNull
	@Override
	public String getName(){
		return "Hermitcraft video fetcher";
	}
	
	@NotNull
	@Override
	public TimeUnit getPeriodUnit(){
		return MINUTES;
	}
}
