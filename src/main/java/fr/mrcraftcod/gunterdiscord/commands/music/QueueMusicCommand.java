package fr.mrcraftcod.gunterdiscord.commands.music;

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
import java.util.concurrent.atomic.AtomicInteger;

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
	}
	
	@Override
	public CommandResult execute(@NotNull final MessageReceivedEvent event, @NotNull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		var position = new AtomicInteger(0);
		EmbedBuilder builder = Utilities.buildEmbed(event.getAuthor(), Color.PINK, "File d'attente des musiques (10 max)");
		GunterAudioManager.getQueue(event.getGuild()).stream().limit(10).forEach(track -> {
			var userData = track.getUserData(TrackUserFields.class);
			builder.addField("Position " + position.addAndGet(1), track.getInfo().title + "\nDemandé par: " + userData.get(new RequesterTrackUserField()).map(User::getAsMention).orElse("Inconnu"), false);
		});
		Actions.reply(event, builder.build());
		return CommandResult.SUCCESS;
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
