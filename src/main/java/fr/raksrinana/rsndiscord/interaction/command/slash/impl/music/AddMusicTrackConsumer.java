package fr.raksrinana.rsndiscord.interaction.command.slash.impl.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fr.raksrinana.rsndiscord.music.RSNAudioManager;
import fr.raksrinana.rsndiscord.music.TrackConsumer;
import fr.raksrinana.rsndiscord.music.trackfields.ReplayTrackDataField;
import fr.raksrinana.rsndiscord.music.trackfields.TrackUserFields;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Objects;
import static fr.raksrinana.rsndiscord.interaction.command.slash.impl.music.NowPlayingCommand.getDuration;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.awt.Color.GREEN;

public class AddMusicTrackConsumer implements TrackConsumer{
	private final Guild guild;
	private final SlashCommandInteraction event;
	private final boolean repeat;
	private final User author;
	
	public AddMusicTrackConsumer(@NotNull Guild guild, @NotNull SlashCommandInteraction event, @NotNull User author, boolean repeat){
		this.guild = guild;
		this.event = event;
		this.author = author;
		this.repeat = repeat;
	}
	
	@Override
	public void onPlaylist(@NotNull List<AudioTrack> tracks){
		tracks.forEach(this::onTrack);
	}
	
	@Override
	public void onTrack(@NotNull AudioTrack track){
		if(repeat){
			if(track.getUserData() instanceof TrackUserFields){
				track.getUserData(TrackUserFields.class).fill(new ReplayTrackDataField(), true);
			}
		}
		var queue = RSNAudioManager.getQueue(guild);
		var before = queue.stream()
				.takeWhile(t -> !Objects.equals(track, t))
				.toList();
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
				.addField(translate(guild, "music.track.duration"), getDuration(track.getDuration()), true)
				.addField(translate(guild, "music.track.eta"), getDuration(delay), true)
				.addField(translate(guild, "music.repeating"), String.valueOf(repeat), true);
		if(!isCurrentTrack){
			builder.addField(translate(guild, "music.queue.position"), String.valueOf(1 + before.size()), true);
		}
		JDAWrappers.reply(event, builder.build()).submit();
	}
	
	@Override
	public void onFailure(@NotNull String message){
		JDAWrappers.reply(event, message).submit();
	}
}
