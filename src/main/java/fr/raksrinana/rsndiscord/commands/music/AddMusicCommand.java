package fr.raksrinana.rsndiscord.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.music.RSNAudioManager;
import fr.raksrinana.rsndiscord.utils.music.trackfields.ReplayTrackDataField;
import fr.raksrinana.rsndiscord.utils.music.trackfields.TrackUserFields;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import static fr.raksrinana.rsndiscord.commands.music.NowPlayingMusicCommand.getDuration;

public class AddMusicCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	AddMusicCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("link", "Music link", false);
		builder.addField("skip", "The number of tracks to skip before adding them", false);
		builder.addField("max", "The maximum number of tracks to add", false);
		builder.addField("repeat", "Either to repeat this track or not (true/false)", false);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		if(args.isEmpty()){
			return CommandResult.BAD_ARGUMENTS;
		}
		else if(Optional.ofNullable(event.getMember()).map(Member::getVoiceState).map(GuildVoiceState::inVoiceChannel).orElse(false)){
			Optional.ofNullable(event.getMember()).map(Member::getVoiceState).map(GuildVoiceState::getChannel).ifPresent(voiceChannel -> {
				final var identifier = Objects.requireNonNull(args.poll()).trim();
				final var skipCount = Optional.ofNullable(args.poll()).map(value -> {
					try{
						return Integer.parseInt(value);
					}
					catch(Exception ignored){
					}
					return 0;
				}).filter(value -> value >= 0).orElse(0);
				final var maxTracks = Optional.ofNullable(args.poll()).map(value -> {
					try{
						return Integer.parseInt(value);
					}
					catch(Exception ignored){
					}
					return 10;
				}).filter(value -> value >= 0).orElse(10);
				final var repeat = Optional.ofNullable(args.poll()).map(Boolean::valueOf).orElse(false);
				final Consumer<AudioTrack> onTrackAdded = audioTrack -> {
					if(Objects.isNull(audioTrack)){
						Actions.reply(event, "Unknown music", null);
					}
					else{
						if(repeat){
							if(audioTrack.getUserData() instanceof TrackUserFields){
								audioTrack.getUserData(TrackUserFields.class).fill(new ReplayTrackDataField(), true);
							}
						}
						final var queue = RSNAudioManager.getQueue(event.getGuild());
						final var before = queue.stream().takeWhile(t -> !Objects.equals(audioTrack, t)).collect(Collectors.toList());
						final var embed = Utilities.buildEmbed(event.getAuthor(), Color.GREEN, "Music added", audioTrack.getInfo().uri);
						final var isCurrentTrack = RSNAudioManager.currentTrack(event.getGuild()).map(trk -> Objects.equals(trk, audioTrack)).orElse(false);
						embed.setDescription(audioTrack.getInfo().title);
						embed.addField("Requester", event.getAuthor().getAsMention(), true);
						embed.addField("Duration", NowPlayingMusicCommand.getDuration(audioTrack.getDuration()), true);
						embed.addField("ETA", getDuration(RSNAudioManager.currentTrack(event.getGuild()).map(t -> t.getDuration() - t.getPosition()).filter(e -> !queue.isEmpty()).orElse(0L) + before.stream().mapToLong(AudioTrack::getDuration).sum()), true);
						embed.addField("Repeating", String.valueOf(repeat), true);
						if(!isCurrentTrack){
							embed.addField("Position in queue", String.valueOf(1 + before.size()), true);
						}
						Actions.reply(event, "", embed.build());
					}
				};
				final Consumer<List<AudioTrack>> onPlaylistAdded = playlist -> playlist.forEach(onTrackAdded);
				RSNAudioManager.play(event.getAuthor(), voiceChannel, null, onTrackAdded, onPlaylistAdded, error -> Actions.reply(event, error, null), skipCount, maxTracks, identifier);
			});
		}
		else{
			Actions.reply(event, "You must be in a voice channel", null);
		}
		return CommandResult.SUCCESS;
	}
	
	@NonNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <link> [skip] [max] [repeat]";
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Add";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("add", "a");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Adds a music to the queue";
	}
}
