package fr.mrcraftcod.gunterdiscord.commands.music;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import fr.mrcraftcod.gunterdiscord.utils.player.GunterAudioManager;
import fr.mrcraftcod.gunterdiscord.utils.player.trackfields.ReplayTrackUserField;
import fr.mrcraftcod.gunterdiscord.utils.player.trackfields.RequesterTrackUserField;
import fr.mrcraftcod.gunterdiscord.utils.player.trackfields.TrackUserFields;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Color;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-06-16
 */
public class MoveMusicCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	MoveMusicCommand(@Nullable final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@Nonnull final Guild guild, @Nonnull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("link", "Music link", false);
		builder.addField("skip", "The number of tracks to skip before adding them", false);
		builder.addField("max", "The maximum number of tracks to add", false);
		builder.addField("repeat", "Either to repeat this track or not (true/false)", false);
	}
	
	@SuppressWarnings("DuplicatedCode")
	@Nonnull
	@Override
	public CommandResult execute(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		if(args.isEmpty()){
			Actions.reply(event, "Please give a music position from the queue");
		}
		else if(Optional.ofNullable(event.getMember()).map(Member::getVoiceState).map(GuildVoiceState::inVoiceChannel).orElse(false)){
			final var queue = GunterAudioManager.getQueue(event.getGuild());
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
			final var builder = Utilities.buildEmbed(event.getAuthor(), Color.CYAN, "Moved music");
			builder.setTitle("Moved music", track.getInfo().uri);
			final var userData = track.getUserData(TrackUserFields.class);
			builder.setDescription(track.getInfo().title);
			builder.addField("Requester", userData.get(new RequesterTrackUserField()).map(User::getAsMention).orElse("Unknown"), true);
			builder.addField("Repeating", userData.get(new ReplayTrackUserField()).map(Object::toString).orElse("False"), true);
			Actions.reply(event, "Moved {} to position {}", track.getInfo().title, moveToPosition + 1);
		}
		else{
			Actions.reply(event, "You must be in a voice channel");
		}
		return CommandResult.SUCCESS;
	}
	
	@Nonnull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <current position in queue> [new position in queue]";
	}
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.MODERATOR;
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Move";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("move");
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return "Move a music in the queue";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
