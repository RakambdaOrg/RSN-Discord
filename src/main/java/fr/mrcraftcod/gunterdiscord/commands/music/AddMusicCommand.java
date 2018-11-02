package fr.mrcraftcod.gunterdiscord.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.player.GunterAudioManager;
import fr.mrcraftcod.gunterdiscord.utils.player.trackfields.ReplayTrackUserField;
import fr.mrcraftcod.gunterdiscord.utils.player.trackfields.TrackUserFields;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
	AddMusicCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NotNull final Guild guild, @NotNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("link", "Music link", false);
		builder.addField("skip", "The number of tracks to skip before adding them", false);
		builder.addField("max", "The maximum number of tracks to add", false);
		builder.addField("repeat", "Either to repeat this track or not (true/false)", false);
	}
	
	@Override
	public CommandResult execute(@NotNull final MessageReceivedEvent event, @NotNull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		if(args.isEmpty()){
			Actions.reply(event, "Please give a link");
		}
		else if(event.getMember().getVoiceState().inVoiceChannel()){
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
			GunterAudioManager.play(event.getAuthor(), event.getMember().getVoiceState().getChannel(), null, track -> {
				if(Objects.isNull(track)){
					Actions.reply(event, "%s, unknown music", event.getAuthor().getAsMention());
				}
				else if(track instanceof AudioTrack){
					final var audioTrack = (AudioTrack) track;
					if(repeat){
						if(audioTrack.getUserData() instanceof TrackUserFields){
							audioTrack.getUserData(TrackUserFields.class).fill(new ReplayTrackUserField(), true);
						}
					}
					final var queue = GunterAudioManager.getQueue(event.getGuild());
					final var before = queue.stream().takeWhile(t -> !track.equals(t)).collect(Collectors.toList());
					Actions.reply(event, "%s added %s, position in queue: %d, ETA: %s", event.getAuthor().getAsMention(), ((AudioTrack) track).getInfo().title, GunterAudioManager.currentTrack(event.getGuild()).equals(track) ? 0 : (1 + before.size()), getDuration(GunterAudioManager.currentTrack(event.getGuild()).map(t -> t.getDuration() - t.getPosition()).filter(e -> !queue.isEmpty()).orElse(0L) + before.stream().mapToLong(AudioTrack::getDuration).sum()));
				}
				else{
					Actions.reply(event, track.toString());
				}
			}, skipCount, maxTracks, identifier);
		}
		else{
			Actions.reply(event, "You must be in a voice channel");
		}
		return CommandResult.SUCCESS;
	}
	
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <link> [skip] [max] [repeat]";
	}
	
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@Override
	public String getName(){
		return "Add";
	}
	
	@Override
	public List<String> getCommand(){
		return List.of("add", "a");
	}
	
	@Override
	public String getDescription(){
		return "Adds a music to the queue";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
