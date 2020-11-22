package fr.raksrinana.rsndiscord.modules.music.command;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.modules.music.RSNAudioManager;
import fr.raksrinana.rsndiscord.modules.music.trackfields.ReplayTrackDataField;
import fr.raksrinana.rsndiscord.modules.music.trackfields.RequesterTrackDataField;
import fr.raksrinana.rsndiscord.modules.music.trackfields.TrackUserFields;
import fr.raksrinana.rsndiscord.modules.permission.IPermission;
import fr.raksrinana.rsndiscord.modules.permission.SimplePermission;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.commands.generic.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.awt.Color.CYAN;
import static java.awt.Color.RED;

public class NowPlayingMusicCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	NowPlayingMusicCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return new SimplePermission("command.music.currently-playing", true);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
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
		
		event.getChannel().sendMessage(builder.build()).submit();
		return SUCCESS;
	}
	
	@NonNull
	private static String buildBar(double current, final double total){
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
	@NonNull
	static String getDuration(final long time){
		var duration = Duration.ofMillis(time);
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
