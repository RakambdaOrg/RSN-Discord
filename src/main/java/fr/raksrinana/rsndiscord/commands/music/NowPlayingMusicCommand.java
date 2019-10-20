package fr.raksrinana.rsndiscord.commands.music;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.player.RSNAudioManager;
import fr.raksrinana.rsndiscord.utils.player.trackfields.ReplayTrackUserField;
import fr.raksrinana.rsndiscord.utils.player.trackfields.RequesterTrackUserField;
import fr.raksrinana.rsndiscord.utils.player.trackfields.TrackUserFields;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Color;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-06-16
 */
public class NowPlayingMusicCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	NowPlayingMusicCommand(@Nullable final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@Nonnull final Guild guild, @Nonnull final EmbedBuilder builder){
		super.addHelp(guild, builder);
	}
	
	@SuppressWarnings("DuplicatedCode")
	@Nonnull
	@Override
	public CommandResult execute(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		final var builder = Utilities.buildEmbed(event.getAuthor(), Color.CYAN, "Currently playing");
		RSNAudioManager.currentTrack(event.getGuild()).ifPresentOrElse(track -> {
			builder.setTitle("Currently playing", track.getInfo().uri);
			final var userData = track.getUserData(TrackUserFields.class);
			builder.setDescription(track.getInfo().title);
			builder.addField("Requester", userData.get(new RequesterTrackUserField()).map(User::getAsMention).orElse("Unknown"), true);
			builder.addField("Repeating", userData.get(new ReplayTrackUserField()).map(Object::toString).orElse("False"), true);
			builder.addField("Position", String.format("%s %s / %s", this.buildBar(track.getPosition(), track.getDuration()), getDuration(track.getPosition()), getDuration(track.getDuration())), true);
		}, () -> {
			builder.setColor(Color.RED);
			builder.setDescription("No music are currently playing");
		});
		Actions.reply(event, builder.build());
		return CommandResult.SUCCESS;
	}
	
	@Nonnull
	private String buildBar(double current, final double total){
		final var charCount = 10;
		if(current > total){
			current = total;
		}
		final var sb = new StringBuilder();
		IntStream.range(0, charCount).forEach(i -> sb.append('='));
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
	@Nonnull
	static String getDuration(final long time){
		final var duration = Duration.ofMillis(time);
		if(duration.toHoursPart() > 0){
			return String.format("%02d:%02d:%02d", duration.toHoursPart(), duration.toMinutesPart(), duration.toSecondsPart());
		}
		return String.format("%02d:%02d", duration.toMinutesPart(), duration.toSecondsPart());
	}
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Now playing";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		//noinspection SpellCheckingInspection
		return List.of("nowplaying", "np");
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return "Get information about the current music";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
