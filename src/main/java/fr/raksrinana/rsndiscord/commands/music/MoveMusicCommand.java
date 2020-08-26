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
import fr.raksrinana.rsndiscord.utils.permission.Permission;
import fr.raksrinana.rsndiscord.utils.permission.SimplePermission;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.awt.Color;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

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
		builder.addField("from", translate(guild, "command.music.move.help.from"), false);
		builder.addField("to", translate(guild, "command.music.move.help.to"), false);
	}
	
	@Override
	public @NonNull Permission getPermission(){
		return new SimplePermission("command.music.move", false);
	}
	
	@SuppressWarnings("DuplicatedCode")
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		if(args.isEmpty()){
			return CommandResult.BAD_ARGUMENTS;
		}
		final var queue = RSNAudioManager.getQueue(event.getGuild());
		final var moveFromPosition = Optional.ofNullable(args.poll()).map(value -> {
			try{
				return Integer.parseInt(value);
			}
			catch(Exception ignored){
			}
			return 0;
		}).filter(value -> value > 0 && value <= queue.size()).orElseThrow(() -> new IllegalArgumentException("Please give a valid position")) - 1;
		final var moveToPosition = Math.min(Optional.ofNullable(args.poll()).map(value -> {
			try{
				return Integer.parseInt(value);
			}
			catch(Exception ignored){
			}
			return 0;
		}).filter(value -> value > 0).orElse(1), queue.size()) - 1;
		final var track = queue.get(moveFromPosition);
		Collections.rotate(queue.subList(moveFromPosition, moveToPosition + 1), -1);
		final var builder = Utilities.buildEmbed(event.getAuthor(), Color.CYAN, translate(event.getGuild(), "music.track.moved"), null);
		builder.setTitle(translate(event.getGuild(), "music.track.moved"), track.getInfo().uri);
		final var userData = track.getUserData(TrackUserFields.class);
		builder.setDescription(track.getInfo().title);
		builder.addField(translate(event.getGuild(), "music.queue.new-position"), Integer.toString(moveToPosition + 1), true);
		builder.addField(translate(event.getGuild(), "music.requester"), userData.get(new RequesterTrackDataField())
				.map(User::getAsMention)
				.orElseGet(() -> translate(event.getGuild(), "music.unknown-requester")), true);
		builder.addField(translate(event.getGuild(), "music.repeating"), userData.get(new ReplayTrackDataField())
				.map(Object::toString)
				.orElse("False"),
				true);
		Actions.sendEmbed(event.getChannel(), builder.build());
		return CommandResult.SUCCESS;
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
