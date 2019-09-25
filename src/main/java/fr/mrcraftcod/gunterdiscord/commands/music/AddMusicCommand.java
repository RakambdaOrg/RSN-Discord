package fr.mrcraftcod.gunterdiscord.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import fr.mrcraftcod.gunterdiscord.utils.player.RSNAudioManager;
import fr.mrcraftcod.gunterdiscord.utils.player.trackfields.ReplayTrackUserField;
import fr.mrcraftcod.gunterdiscord.utils.player.trackfields.TrackUserFields;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import static fr.mrcraftcod.gunterdiscord.commands.music.NowPlayingMusicCommand.getDuration;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-06-16
 */
public class AddMusicCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	AddMusicCommand(@Nullable final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@Nonnull final Guild guild, @Nonnull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("link", "Music link", false);
		builder.addField("skip", "The number of tracks to skip before adding them", false);
		builder.addField("max", "The maximum number of tracks to add", false);
		builder.addField("repeat", "Either to repeat this track or not (true/false)", false);
	}
	
	@Nonnull
	@Override
	public CommandResult execute(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		if(args.isEmpty()){
			Actions.reply(event, "Please give a link");
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
						Actions.replyFormatted(event, "%s, unknown music", event.getAuthor().getAsMention());
					}
					else{
						if(repeat){
							if(audioTrack.getUserData() instanceof TrackUserFields){
								audioTrack.getUserData(TrackUserFields.class).fill(new ReplayTrackUserField(), true);
							}
						}
						final var queue = RSNAudioManager.getQueue(event.getGuild());
						final var before = queue.stream().takeWhile(t -> !Objects.equals(audioTrack, t)).collect(Collectors.toList());
						final var embed = Utilities.buildEmbed(event.getAuthor(), Color.GREEN, "Music added", audioTrack.getInfo().uri);
						embed.setDescription(audioTrack.getInfo().title);
						embed.addField("Requester", event.getAuthor().getAsMention(), true);
						embed.addField("ETA", getDuration(RSNAudioManager.currentTrack(event.getGuild()).map(t -> t.getDuration() - t.getPosition()).filter(e -> !queue.isEmpty()).orElse(0L) + before.stream().mapToLong(AudioTrack::getDuration).sum()), true);
						embed.addField("Repeating", String.valueOf(repeat), true);
						embed.addField("Position in queue", String.valueOf(RSNAudioManager.currentTrack(event.getGuild()).equals(audioTrack) ? 0 : (1 + before.size())), true);
						Actions.reply(event, embed.build());
					}
				};
				final Consumer<List<AudioTrack>> onPlaylistAdded = playlist -> {
					if(playlist.size() < 10){
						playlist.forEach(onTrackAdded);
					}
					else{
						Actions.replyFormatted(event, "Added %d songs from a playlist", playlist.size());
					}
				};
				RSNAudioManager.play(event.getAuthor(), voiceChannel, null, onTrackAdded, onPlaylistAdded, error -> Actions.reply(event, error), skipCount, maxTracks, identifier);
			});
		}
		else{
			Actions.reply(event, "You must be in a voice channel");
		}
		return CommandResult.SUCCESS;
	}
	
	@Nonnull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <link> [skip] [max] [repeat]";
	}
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Add";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("add", "a");
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return "Adds a music to the queue";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
