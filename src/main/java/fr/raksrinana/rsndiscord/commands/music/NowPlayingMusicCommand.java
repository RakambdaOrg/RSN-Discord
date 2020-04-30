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
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.awt.Color;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;

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
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		final var builder = Utilities.buildEmbed(event.getAuthor(), Color.CYAN, "Currently playing", null);
		RSNAudioManager.currentTrack(event.getGuild()).ifPresentOrElse(track -> {
			builder.setTitle("Currently playing", track.getInfo().uri);
			final var userData = track.getUserData(TrackUserFields.class);
			builder.setDescription(track.getInfo().title);
			builder.addField("Requester", userData.get(new RequesterTrackDataField()).map(User::getAsMention).orElse("Unknown"), true);
			builder.addField("Repeating", userData.get(new ReplayTrackDataField()).map(Object::toString).orElse("False"), true);
			builder.addField("Position", String.format("%s %s / %s", NowPlayingMusicCommand.buildBar(track.getPosition(), track.getDuration()), getDuration(track.getPosition()), getDuration(track.getDuration())), true);
		}, () -> {
			builder.setColor(Color.RED);
			builder.setDescription("No music are currently playing");
		});
		Actions.reply(event, "", builder.build());
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
	public String getName(){
		return "Now playing";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("nowplaying", "np");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Get information about the current music";
	}
}
