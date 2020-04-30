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
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.awt.Color;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

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
		builder.addField("link", "Music link", false);
		builder.addField("skip", "The number of tracks to skip before adding them", false);
		builder.addField("max", "The maximum number of tracks to add", false);
		builder.addField("repeat", "Either to repeat this track or not (true/false)", false);
	}
	
	@SuppressWarnings("DuplicatedCode")
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		if(args.isEmpty()){
			return CommandResult.BAD_ARGUMENTS;
		}
		else if(Optional.ofNullable(event.getMember()).map(Member::getVoiceState).map(GuildVoiceState::inVoiceChannel).orElse(false)){
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
			final var builder = Utilities.buildEmbed(event.getAuthor(), Color.CYAN, "Moved music", null);
			builder.setTitle("Moved music", track.getInfo().uri);
			final var userData = track.getUserData(TrackUserFields.class);
			builder.setDescription(track.getInfo().title);
			builder.addField("Requester", userData.get(new RequesterTrackDataField()).map(User::getAsMention).orElse("Unknown"), true);
			builder.addField("Repeating", userData.get(new ReplayTrackDataField()).map(Object::toString).orElse("False"), true);
			Actions.reply(event, MessageFormat.format("Moved {0} to position {0}", track.getInfo().title, moveToPosition + 1), null);
		}
		else{
			Actions.reply(event, "You must be in a voice channel", null);
		}
		return CommandResult.SUCCESS;
	}
	
	@NonNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <current position in queue> [new position in queue]";
	}
	
	@NonNull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.MODERATOR;
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Move";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("move");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Move a music in the queue";
	}
}
