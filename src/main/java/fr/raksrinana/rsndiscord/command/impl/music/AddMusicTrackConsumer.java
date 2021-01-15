package fr.raksrinana.rsndiscord.command.impl.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fr.raksrinana.rsndiscord.music.RSNAudioManager;
import fr.raksrinana.rsndiscord.music.TrackConsumer;
import fr.raksrinana.rsndiscord.music.trackfields.ReplayTrackDataField;
import fr.raksrinana.rsndiscord.music.trackfields.TrackUserFields;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import static fr.raksrinana.rsndiscord.command.impl.music.NowPlayingMusicCommand.getDuration;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.awt.Color.GREEN;
import static java.util.Objects.isNull;

public class AddMusicTrackConsumer implements TrackConsumer{
	private final Guild guild;
	private final TextChannel channel;
	private final boolean repeat;
	private final User author;
	
	public AddMusicTrackConsumer(Guild guild, TextChannel channel, User author, boolean repeat){
		this.guild = guild;
		this.channel = channel;
		this.author = author;
		this.repeat = repeat;
	}
	
	@Override
	public void onPlaylist(List<AudioTrack> tracks){
		tracks.forEach(this::onTrack);
	}
	
	@Override
	public void onTrack(AudioTrack track){
		if(isNull(track)){
			channel.sendMessage(translate(guild, "music.track.unknown")).submit();
		}
		else{
			if(repeat){
				if(track.getUserData() instanceof TrackUserFields){
					track.getUserData(TrackUserFields.class).fill(new ReplayTrackDataField(), true);
				}
			}
			var queue = RSNAudioManager.getQueue(guild);
			var before = queue.stream()
					.takeWhile(t -> !Objects.equals(track, t))
					.collect(Collectors.toList());
			var isCurrentTrack = RSNAudioManager.currentTrack(guild)
					.map(trk -> Objects.equals(trk, track))
					.orElse(false);
			
			var currentTrackTimeLeft = RSNAudioManager.currentTrack(guild)
					.map(t -> t.getDuration() - t.getPosition())
					.filter(e -> !queue.isEmpty())
					.orElse(0L);
			var queueTime = before.stream()
					.mapToLong(AudioTrack::getDuration)
					.sum();
			var delay = currentTrackTimeLeft + queueTime;
			
			var builder = new EmbedBuilder().setAuthor(author.getName(), null, author.getAvatarUrl())
					.setColor(GREEN)
					.setTitle(translate(guild, "music.track.added"), track.getInfo().uri)
					.setDescription(track.getInfo().title)
					.addField(translate(guild, "music.requester"), author.getAsMention(), true)
					.addField(translate(guild, "music.track.duration"), NowPlayingMusicCommand.getDuration(track.getDuration()), true)
					.addField(translate(guild, "music.track.eta"), getDuration(delay), true)
					.addField(translate(guild, "music.repeating"), String.valueOf(repeat), true);
			if(!isCurrentTrack){
				builder.addField(translate(guild, "music.queue.position"), String.valueOf(1 + before.size()), true);
			}
			channel.sendMessage(builder.build()).submit();
		}
	}
	
	@Override
	public void onFailure(String message){
		channel.sendMessage(message).submit();
	}
}
