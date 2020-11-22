package fr.raksrinana.rsndiscord.modules.music.command;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.modules.music.RSNAudioManager;
import fr.raksrinana.rsndiscord.modules.music.trackfields.ReplayTrackDataField;
import fr.raksrinana.rsndiscord.modules.music.trackfields.RequesterTrackDataField;
import fr.raksrinana.rsndiscord.modules.music.trackfields.TrackUserFields;
import fr.raksrinana.rsndiscord.modules.permission.IPermission;
import fr.raksrinana.rsndiscord.modules.permission.SimplePermission;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.commands.generic.CommandResult.BAD_ARGUMENTS;
import static fr.raksrinana.rsndiscord.commands.generic.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.awt.Color.CYAN;

public class MoveMusicCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	MoveMusicCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("from", translate(guild, "command.music.move.help.from"), false)
				.addField("to", translate(guild, "command.music.move.help.to"), false);
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return new SimplePermission("command.music.move", false);
	}
	
	@SuppressWarnings("DuplicatedCode")
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		if(args.isEmpty()){
			return BAD_ARGUMENTS;
		}
		
		var author = event.getAuthor();
		
		var queue = RSNAudioManager.getQueue(event.getGuild());
		var moveFromPosition = getArgumentAsInteger(args)
				.filter(value -> value > 0 && value <= queue.size())
				.map(val -> val - 1)
				.orElseThrow(() -> new IllegalArgumentException("Please give a valid position"));
		var moveToPosition = -1 + Math.min(queue.size(), getArgumentAsInteger(args)
				.filter(value -> value > 0)
				.orElse(1));
		var track = queue.get(moveFromPosition);
		var userData = track.getUserData(TrackUserFields.class);
		
		Collections.rotate(queue.subList(moveFromPosition, moveToPosition + 1), -1);
		
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
		event.getChannel().sendMessage(embed).submit();
		return SUCCESS;
	}
	
	@NonNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <from> [to]";
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.music.move.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("move");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.music.move.description");
	}
}
