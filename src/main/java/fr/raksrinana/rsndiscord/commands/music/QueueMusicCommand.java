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
import fr.raksrinana.rsndiscord.utils.permission.Permission;
import fr.raksrinana.rsndiscord.utils.permission.SimplePermission;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

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
		builder.addField("<page>", translate(guild, "command.music.queue.help.page"), false);
	}
	
	@Override
	public @NonNull Permission getPermission(){
		return new SimplePermission("command.music.queue", true);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		final var perPage = 10L;
		final var page = Optional.ofNullable(args.poll()).map(pageStr -> {
			try{
				return Long.parseLong(pageStr);
			}
			catch(final Exception ignored){
			}
			return null;
		}).orElse(1L) - 1;
		final var position = new AtomicLong(perPage * page);
		final var queue = RSNAudioManager.getQueue(event.getGuild());
		var maxPageNumber = (int) Math.ceil(queue.size() / (double) perPage);
		final var builder = Utilities.buildEmbed(event.getAuthor(), Color.PINK, translate(event.getGuild(), "music.queue.page", page + 1, maxPageNumber), null);
		builder.setDescription(translate(event.getGuild(), "music.queue.size", queue.size()));
		final var beforeDuration = new AtomicLong(RSNAudioManager.currentTrack(event.getGuild())
				.map(t -> t.getDuration() - t.getPosition())
				.orElse(0L)
				+ queue.stream().limit(perPage * page)
				.mapToLong(AudioTrack::getDuration)
				.sum());
		queue.stream()
				.skip(perPage * page)
				.limit(perPage)
				.forEachOrdered(track -> {
					final var userData = track.getUserData(TrackUserFields.class);
					builder.addField("Position " + position.addAndGet(1),
							track.getInfo().title +
									"\n" + translate(event.getGuild(), "music.requester") + ": " + userData.get(new RequesterTrackDataField())
									.map(User::getAsMention)
									.orElseGet(() -> translate(event.getGuild(), "music.unknown-requester")) +
									"\n" + translate(event.getGuild(), "music.repeating") + ": " + userData.get(new ReplayTrackDataField())
									.map(Object::toString)
									.orElse("False") +
									"\n" + translate(event.getGuild(), "music.track.duration") + ": " + NowPlayingMusicCommand.getDuration(track.getDuration()) +
									"\n" + translate(event.getGuild(), "music.track.eta") + ": " + NowPlayingMusicCommand.getDuration(beforeDuration.get()),
							false);
					beforeDuration.addAndGet(track.getDuration());
				});
		Actions.sendEmbed(event.getChannel(), builder.build());
		return CommandResult.SUCCESS;
	}
	
	@NonNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <page>";
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.music.queue.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("queue", "q");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.music.queue.description");
	}
}
