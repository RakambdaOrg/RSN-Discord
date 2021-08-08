package fr.raksrinana.rsndiscord.command.impl.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.command.base.group.SubCommand;
import fr.raksrinana.rsndiscord.music.RSNAudioManager;
import fr.raksrinana.rsndiscord.music.trackfields.ReplayTrackDataField;
import fr.raksrinana.rsndiscord.music.trackfields.RequesterTrackDataField;
import fr.raksrinana.rsndiscord.music.trackfields.TrackUserFields;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import static fr.raksrinana.rsndiscord.command.CommandResult.HANDLED;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.awt.Color.PINK;
import static net.dv8tion.jda.api.interactions.commands.OptionType.INTEGER;

public class QueueCommand extends SubCommand{
	private static final String PAGE_OPTION_ID = "page";
	
	@Override
	@NotNull
	public String getId(){
		return "queue";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Displays the queue";
	}
	
	@Override
	@NotNull
	protected Collection<? extends OptionData> getOptions(){
		return List.of(new OptionData(INTEGER, PAGE_OPTION_ID, "Page to get"));
	}
	
	@Override
	@NotNull
	public CommandResult execute(@NotNull SlashCommandEvent event){
		var guild = event.getGuild();
		var author = event.getUser();
		var queue = RSNAudioManager.getQueue(guild);
		var perPage = 10L;
		var maxPageNumber = (int) Math.ceil(queue.size() / (double) perPage);
		
		long page = getOptionAsInt(event.getOption(PAGE_OPTION_ID))
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
							"\n" + translate(guild, "music.track.duration") + ": " + NowPlayingCommand.getDuration(track.getDuration()) +
							"\n" + translate(guild, "music.track.eta") + ": " + NowPlayingCommand.getDuration(beforeDuration.get());
					
					builder.addField("Position " + position.addAndGet(1), trackInfo, false);
					beforeDuration.addAndGet(track.getDuration());
				});
		
		JDAWrappers.edit(event, builder.build()).submit();
		return HANDLED;
	}
}
