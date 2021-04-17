package fr.raksrinana.rsndiscord.command.impl.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
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
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.awt.Color.PINK;

public class QueueMusicCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	QueueMusicCommand(@NotNull Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NotNull Guild guild, @NotNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("<page>", translate(guild, "command.music.queue.help.page"), false);
	}
	
	@NotNull
	@Override
	public CommandResult execute(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
		super.execute(event, args);
		
		var guild = event.getGuild();
		var author = event.getAuthor();
		var queue = RSNAudioManager.getQueue(guild);
		var perPage = 10L;
		var maxPageNumber = (int) Math.ceil(queue.size() / (double) perPage);
		
		long page = getArgumentAsLong(args)
				.map(val -> val - 1L)
				.orElse(0L);
		var position = new AtomicLong(perPage * page);
		
		var currentTrackTimeLeft = RSNAudioManager.currentTrack(guild)
				.map(t -> t.getDuration() - t.getPosition())
				.orElse(0L);
		var queueDuration = queue.stream().limit(perPage * page)
				.mapToLong(AudioTrack::getDuration)
				.sum();
		var beforeDuration = new AtomicLong(currentTrackTimeLeft + queueDuration);
		
		var builder = new EmbedBuilder().setAuthor(author.getName(), null, author.getAvatarUrl())
				.setColor(PINK)
				.setTitle(translate(guild, "music.queue.page", page + 1, maxPageNumber))
				.setDescription(translate(guild, "music.queue.size", queue.size()));
		
		queue.stream().skip(perPage * page)
				.limit(perPage)
				.forEachOrdered(track -> {
					var userData = track.getUserData(TrackUserFields.class);
					var requester = userData.get(new RequesterTrackDataField())
							.map(User::getAsMention)
							.orElseGet(() -> translate(guild, "music.unknown-requester"));
					var repeating = userData.get(new ReplayTrackDataField())
							.map(Object::toString)
							.orElse("False");
					
					var trackInfo = track.getInfo().title +
							"\n" + translate(guild, "music.requester") + ": " + requester +
							"\n" + translate(guild, "music.repeating") + ": " + repeating +
							"\n" + translate(guild, "music.track.duration") + ": " + NowPlayingMusicCommand.getDuration(track.getDuration()) +
							"\n" + translate(guild, "music.track.eta") + ": " + NowPlayingMusicCommand.getDuration(beforeDuration.get());
					
					builder.addField("Position " + position.addAndGet(1), trackInfo, false);
					beforeDuration.addAndGet(track.getDuration());
				});
		
		JDAWrappers.message(event, builder.build()).submit();
		return SUCCESS;
	}
	
	@NotNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <page>";
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return new SimplePermission("command.music.queue", true);
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return translate(guild, "command.music.queue.name");
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return translate(guild, "command.music.queue.description");
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("queue", "q");
	}
}
