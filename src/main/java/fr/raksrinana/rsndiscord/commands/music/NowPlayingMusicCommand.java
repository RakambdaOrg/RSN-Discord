package fr.raksrinana.rsndiscord.commands.music;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.music.RSNAudioManager;
import fr.raksrinana.rsndiscord.utils.music.trackfields.ReplayTrackDataField;
import fr.raksrinana.rsndiscord.utils.music.trackfields.RequesterTrackDataField;
import fr.raksrinana.rsndiscord.utils.music.trackfields.TrackUserFields;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.awt.Color;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

public class NowPlayingMusicCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	NowPlayingMusicCommand(final Command parent){
		super(parent);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		final var builder = Utilities.buildEmbed(event.getAuthor(), Color.CYAN, translate(event.getGuild(), "music.currently-playing"), null);
		RSNAudioManager.currentTrack(event.getGuild()).ifPresentOrElse(track -> {
			builder.setTitle(translate(event.getGuild(), "music.currently-playing"), track.getInfo().uri);
			final var userData = track.getUserData(TrackUserFields.class);
			builder.setDescription(track.getInfo().title);
			builder.addField(translate(event.getGuild(), "music.requester"), userData.get(new RequesterTrackDataField())
					.map(User::getAsMention)
					.orElseGet(() -> translate(event.getGuild(), "music.unknown-requester")),
					true);
			builder.addField(translate(event.getGuild(), "music.repeating"), userData.get(new ReplayTrackDataField())
					.map(Object::toString)
					.orElse("False"),
					true);
			builder.addField(translate(event.getGuild(), "music.position"), String.format("%s %s / %s", NowPlayingMusicCommand.buildBar(track.getPosition(), track.getDuration()), getDuration(track.getPosition()), getDuration(track.getDuration())), true);
		}, () -> {
			builder.setColor(Color.RED);
			builder.setDescription(translate(event.getGuild(), "music.nothing-playing"));
		});
		Actions.sendEmbed(event.getChannel(), builder.build());
		return CommandResult.SUCCESS;
	}
	
	@NonNull
	private static String buildBar(double current, final double total){
		final var charCount = 10;
		if(current > total){
			current = total;
		}
		final var sb = new StringBuilder("=".repeat(charCount));
		final var replaceIndex = (int) ((current / total) * charCount);
		sb.replace(replaceIndex, replaceIndex + 1, "O");
		return sb.toString();
	}
	
	/**
	 * Get the duration in a readable format.
	 *
	 * @param time The duration in milliseconds.
	 *
	 * @return A readable version of this duration.
	 */
	@NonNull
	static String getDuration(final long time){
		final var duration = Duration.ofMillis(time);
		if(duration.toHoursPart() > 0){
			return String.format("%02d:%02d:%02d", duration.toHoursPart(), duration.toMinutesPart(), duration.toSecondsPart());
		}
		return String.format("%02d:%02d", duration.toMinutesPart(), duration.toSecondsPart());
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.music.now-playing.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("nowplaying", "np");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.music.now-playing.description");
	}
}
