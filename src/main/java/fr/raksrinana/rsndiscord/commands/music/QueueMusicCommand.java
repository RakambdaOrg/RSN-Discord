package fr.raksrinana.rsndiscord.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
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
import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-06-16
 */
public class QueueMusicCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	QueueMusicCommand(@Nonnull final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@Nonnull final Guild guild, @Nonnull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("<page>", "The page to display", false);
	}
	
	@Nonnull
	@Override
	public CommandResult execute(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		final var perPage = 10;
		final var page = Optional.ofNullable(args.poll()).map(pageStr -> {
			try{
				return Integer.parseInt(pageStr);
			}
			catch(final Exception ignored){
			}
			return null;
		}).orElse(1) - 1;
		final var position = new AtomicInteger(perPage * page);
		final var queue = RSNAudioManager.getQueue(event.getGuild());
		final var builder = Utilities.buildEmbed(event.getAuthor(), Color.PINK, "Music queue (Page " + (page + 1) + "/" + ((int) Math.ceil(queue.size() / (double) perPage)) + " - 10 max)");
		builder.setDescription(String.format("%d musics queued", queue.size()));
		final var beforeDuration = new AtomicLong(RSNAudioManager.currentTrack(event.getGuild()).map(t -> t.getDuration() - t.getPosition()).orElse(0L) + queue.stream().limit(perPage * page).mapToLong(AudioTrack::getDuration).sum());
		queue.stream().skip(perPage * page).limit(perPage).forEachOrdered(track -> {
			final var userData = track.getUserData(TrackUserFields.class);
			builder.addField("Position " + position.addAndGet(1), track.getInfo().title + "\nRequester: " + userData.get(new RequesterTrackUserField()).map(User::getAsMention).orElse("Unknown") + "\nRepeating: " + userData.get(new ReplayTrackUserField()).map(Object::toString).orElse("False") + "\nLength: " + NowPlayingMusicCommand.getDuration(track.getDuration()) + "\nETA: " + NowPlayingMusicCommand.getDuration(beforeDuration.get()), false);
			beforeDuration.addAndGet(track.getDuration());
		});
		Actions.reply(event, builder.build());
		return CommandResult.SUCCESS;
	}
	
	@Nonnull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <page>";
	}
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Queue";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("queue", "q");
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return "Prints the music queue";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
