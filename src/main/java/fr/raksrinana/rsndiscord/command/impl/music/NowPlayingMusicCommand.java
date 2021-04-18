package fr.raksrinana.rsndiscord.command.impl.music;

import fr.raksrinana.rsndiscord.command.BasicCommand;
import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.music.RSNAudioManager;
import fr.raksrinana.rsndiscord.music.trackfields.ReplayTrackDataField;
import fr.raksrinana.rsndiscord.music.trackfields.RequesterTrackDataField;
import fr.raksrinana.rsndiscord.music.trackfields.TrackUserFields;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.awt.Color.CYAN;
import static java.awt.Color.RED;

public class NowPlayingMusicCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	NowPlayingMusicCommand(Command parent){
		super(parent);
	}
	
	@NotNull
	@Override
	public CommandResult execute(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
		super.execute(event, args);
		var guild = event.getGuild();
		
		var builder = new EmbedBuilder().setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl())
				.setColor(CYAN);
		
		RSNAudioManager.currentTrack(guild).ifPresentOrElse(track -> {
			var userData = track.getUserData(TrackUserFields.class);
			
			var requester = userData.get(new RequesterTrackDataField())
					.map(User::getAsMention)
					.orElseGet(() -> translate(guild, "music.unknown-requester"));
			var repeating = userData.get(new ReplayTrackDataField())
					.map(Object::toString)
					.orElse("False");
			var progressBar = String.format("%s %s / %s",
					NowPlayingMusicCommand.buildBar(track.getPosition(), track.getDuration()),
					getDuration(track.getPosition()),
					getDuration(track.getDuration()));
			
			builder.setTitle(translate(guild, "music.currently-playing"), track.getInfo().uri)
					.setDescription(track.getInfo().title)
					.addField(translate(guild, "music.requester"), requester, true)
					.addField(translate(guild, "music.repeating"), repeating, true)
					.addField(translate(guild, "music.position"), progressBar, true);
		}, () -> {
			builder.setColor(RED);
			builder.setDescription(translate(guild, "music.nothing-playing"));
		});
		
		JDAWrappers.message(event, builder.build()).submit();
		return SUCCESS;
	}
	
	@NotNull
	private static String buildBar(double current, double total){
		var charCount = 10;
		if(current > total){
			current = total;
		}
		var sb = new StringBuilder("=".repeat(charCount));
		var replaceIndex = (int) ((current / total) * charCount);
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
	@NotNull
	static String getDuration(long time){
		var duration = Duration.ofMillis(time);
		if(duration.toHoursPart() > 0){
			return String.format("%02d:%02d:%02d", duration.toHoursPart(), duration.toMinutesPart(), duration.toSecondsPart());
		}
		return String.format("%02d:%02d", duration.toMinutesPart(), duration.toSecondsPart());
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return new SimplePermission("command.music.currently-playing", true);
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return translate(guild, "command.music.now-playing.name");
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return translate(guild, "command.music.now-playing.description");
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("nowplaying", "np");
	}
}
