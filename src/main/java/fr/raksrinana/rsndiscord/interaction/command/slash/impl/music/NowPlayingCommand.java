package fr.raksrinana.rsndiscord.interaction.command.slash.impl.music;

import fr.raksrinana.rsndiscord.interaction.command.CommandResult;
import fr.raksrinana.rsndiscord.interaction.command.slash.base.group.SubSlashCommand;
import fr.raksrinana.rsndiscord.music.RSNAudioManager;
import fr.raksrinana.rsndiscord.music.trackfields.ReplayTrackDataField;
import fr.raksrinana.rsndiscord.music.trackfields.RequesterTrackDataField;
import fr.raksrinana.rsndiscord.music.trackfields.TrackUserFields;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.jetbrains.annotations.NotNull;
import java.time.Duration;
import static fr.raksrinana.rsndiscord.interaction.command.CommandResult.HANDLED;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.awt.Color.CYAN;
import static java.awt.Color.RED;

public class NowPlayingCommand extends SubSlashCommand{
	@Override
	@NotNull
	public String getId(){
		return "playing";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Displays the current playing music";
	}
	
	@Override
	@NotNull
	public CommandResult executeGuild(@NotNull SlashCommandInteraction event, @NotNull Guild guild, @NotNull Member member){
		var builder = new EmbedBuilder().setColor(CYAN);
		
		RSNAudioManager.currentTrack(guild).ifPresentOrElse(track -> {
			var userData = track.getUserData(TrackUserFields.class);
			
			var requester = userData.get(new RequesterTrackDataField())
					.map(User::getAsMention)
					.orElseGet(() -> translate(guild, "music.unknown-requester"));
			var repeating = userData.get(new ReplayTrackDataField())
					.map(Object::toString)
					.orElse("False");
			var progressBar = String.format("%s %s / %s",
					NowPlayingCommand.buildBar(track.getPosition(), track.getDuration()),
					getDuration(track.getPosition()),
					getDuration(track.getDuration()));
			
			builder.setTitle(translate(guild, "music.currently-playing"), track.getInfo().uri)
					.setDescription(track.getInfo().title)
					.addField(translate(guild, "music.requester"), requester, true)
					.addField(translate(guild, "music.repeating"), repeating, true)
					.addField(translate(guild, "music.position"), progressBar, true)
					.setImage(track.getInfo().artworkUrl);
		}, () -> {
			builder.setColor(RED);
			builder.setDescription(translate(guild, "music.nothing-playing"));
		});
		
		JDAWrappers.edit(event, builder.build()).submit();
		return HANDLED;
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
	public static String getDuration(long time){
		var duration = Duration.ofMillis(time);
		if(duration.toHoursPart() > 0){
			return String.format("%02d:%02d:%02d", duration.toHoursPart(), duration.toMinutesPart(), duration.toSecondsPart());
		}
		return String.format("%02d:%02d", duration.toMinutesPart(), duration.toSecondsPart());
	}
}
