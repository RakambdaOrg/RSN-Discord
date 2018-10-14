package fr.mrcraftcod.gunterdiscord.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import fr.mrcraftcod.gunterdiscord.utils.player.GunterAudioManager;
import fr.mrcraftcod.gunterdiscord.utils.player.trackfields.RequesterTrackUserField;
import fr.mrcraftcod.gunterdiscord.utils.player.trackfields.TrackUserFields;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import static fr.mrcraftcod.gunterdiscord.commands.music.NowPlayingMusicCommand.getDuration;

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
	QueueMusicCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NotNull final Guild guild, @NotNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("<page>", "La page à afficher", false);
	}
	
	@Override
	public CommandResult execute(@NotNull final MessageReceivedEvent event, @NotNull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		final var perPage = 10;
		final var page = Optional.ofNullable(args.poll()).map(i -> {
			try{
				return Integer.parseInt(i);
			}
			catch(final Exception ignored){
			}
			return null;
		}).orElse(1) - 1;
		final var position = new AtomicInteger(0);
		final var queue = GunterAudioManager.getQueue(event.getGuild());
		final var builder = Utilities.buildEmbed(event.getAuthor(), Color.PINK, "File d'attente des musiques (Page " + page + "/" + ((int) Math.ceil(queue.size() / (double) perPage)) + " - 10 max)");
		builder.setDescription(String.format("%d musiques en attente", queue.size()));
		final var beforeDuration = new AtomicLong(GunterAudioManager.currentTrack(event.getGuild()).map(t -> t.getDuration() - t.getPosition()).orElse(0L) + queue.stream().limit(perPage * page).mapToLong(AudioTrack::getDuration).sum());
		queue.stream().skip(perPage * page).limit(perPage).forEachOrdered(track -> {
			final var userData = track.getUserData(TrackUserFields.class);
			builder.addField("Position " + position.addAndGet(1), track.getInfo().title + "\nDemandé par: " + userData.get(new RequesterTrackUserField()).map(User::getAsMention).orElse("Inconnu") + "\nLongeur: " + getDuration(track.getDuration()) + "\nTemps estimé avant lecture: " + getDuration(beforeDuration.get()), false);
			beforeDuration.addAndGet(track.getDuration());
		});
		Actions.reply(event, builder.build());
		return CommandResult.SUCCESS;
	}
	
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <page>";
	}
	
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@Override
	public String getName(){
		return "File d'attente";
	}
	
	@Override
	public List<String> getCommand(){
		return List.of("queue", "q");
	}
	
	@Override
	public String getDescription(){
		return "Obtient la file d'attente";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
