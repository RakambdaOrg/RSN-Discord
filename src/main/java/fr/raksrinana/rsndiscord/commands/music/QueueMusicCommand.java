package fr.raksrinana.rsndiscord.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class QueueMusicCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	QueueMusicCommand(@NonNull final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("<page>", "The page to display", false);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
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
		final var builder = Utilities.buildEmbed(event.getAuthor(), Color.PINK, "Music queue (Page " + (page + 1) + "/" + ((int) Math.ceil(queue.size() / (double) perPage)) + " - 10 max)", null);
		builder.setDescription(String.format("%d musics queued", queue.size()));
		final var beforeDuration = new AtomicLong(RSNAudioManager.currentTrack(event.getGuild()).map(t -> t.getDuration() - t.getPosition()).orElse(0L) + queue.stream().limit(perPage * page).mapToLong(AudioTrack::getDuration).sum());
		queue.stream().skip(perPage * page).limit(perPage).forEachOrdered(track -> {
			final var userData = track.getUserData(TrackUserFields.class);
			builder.addField("Position " + position.addAndGet(1), track.getInfo().title + "\nRequester: " + userData.get(new RequesterTrackDataField()).map(User::getAsMention).orElse("Unknown") + "\nRepeating: " + userData.get(new ReplayTrackDataField()).map(Object::toString).orElse("False") + "\nLength: " + NowPlayingMusicCommand.getDuration(track.getDuration()) + "\nETA: " + NowPlayingMusicCommand.getDuration(beforeDuration.get()), false);
			beforeDuration.addAndGet(track.getDuration());
		});
		Actions.reply(event, "", builder.build());
		return CommandResult.SUCCESS;
	}
	
	@NonNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <page>";
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Queue";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("queue", "q");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Prints the music queue";
	}
}
