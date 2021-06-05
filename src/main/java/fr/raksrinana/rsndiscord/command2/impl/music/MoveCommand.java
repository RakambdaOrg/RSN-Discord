package fr.raksrinana.rsndiscord.command2.impl.music;

import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.command2.base.group.SubCommand;
import fr.raksrinana.rsndiscord.command2.permission.IPermission;
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
import java.util.Collections;
import java.util.List;
import static fr.raksrinana.rsndiscord.command.CommandResult.HANDLED;
import static fr.raksrinana.rsndiscord.command2.permission.SimplePermission.FALSE_BY_DEFAULT;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.awt.Color.CYAN;
import static net.dv8tion.jda.api.interactions.commands.OptionType.INTEGER;

public class MoveCommand extends SubCommand{
	private static final String FROM_OPTION_ID = "from";
	private static final String TO_OPTION_ID = "to";
	
	@Override
	@NotNull
	public String getId(){
		return "move";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Move a track in the queue";
	}
	
	@Override
	public boolean replyEphemeral(){
		return false;
	}
	
	@Override
	@NotNull
	public IPermission getPermission(){
		return FALSE_BY_DEFAULT;
	}
	
	@Override
	@NotNull
	protected Collection<? extends OptionData> getOptions(){
		return List.of(
				new OptionData(INTEGER, FROM_OPTION_ID, "The position of the track to move").setRequired(true),
				new OptionData(INTEGER, TO_OPTION_ID, "The new position of the track (default: end of queue)")
		);
	}
	
	@Override
	@NotNull
	public CommandResult execute(@NotNull SlashCommandEvent event){
		
		var author = event.getUser();
		var queue = RSNAudioManager.getQueue(event.getGuild());
		
		var moveFromPosition = getOptionAsInt(event.getOption(FROM_OPTION_ID))
				.filter(value -> value > 0 && value <= queue.size())
				.map(val -> val - 1)
				.orElseThrow(() -> new IllegalArgumentException("Please give a valid position"));
		var moveToPosition = -1 + Math.min(queue.size(), getOptionAsInt(event.getOption(TO_OPTION_ID))
				.filter(value -> value > 0)
				.orElse(1));
		
		var track = queue.get(moveFromPosition);
		var userData = track.getUserData(TrackUserFields.class);
		
		Collections.rotate(queue.subList(moveToPosition, moveFromPosition + 1), -1);
		
		var requester = userData.get(new RequesterTrackDataField())
				.map(User::getAsMention)
				.orElseGet(() -> translate(event.getGuild(), "music.unknown-requester"));
		var repeating = userData.get(new ReplayTrackDataField())
				.map(Object::toString)
				.orElse("False");
		
		var embed = new EmbedBuilder().setAuthor(author.getName(), null, author.getAvatarUrl())
				.setColor(CYAN)
				.setTitle(translate(event.getGuild(), "music.track.moved"), track.getInfo().uri)
				.setDescription(track.getInfo().title)
				.addField(translate(event.getGuild(), "music.queue.new-position"), Integer.toString(moveToPosition + 1), true)
				.addField(translate(event.getGuild(), "music.requester"), requester, true)
				.addField(translate(event.getGuild(), "music.repeating"), repeating, true)
				.build();
		JDAWrappers.edit(event, embed).submit();
		return HANDLED;
	}
}
