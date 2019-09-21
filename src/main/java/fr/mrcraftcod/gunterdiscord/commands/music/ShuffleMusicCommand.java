package fr.mrcraftcod.gunterdiscord.commands.music;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.player.RSNAudioManager;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-06-16
 */
public class ShuffleMusicCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	ShuffleMusicCommand(@Nullable final Command parent){
		super(parent);
	}
	
	
	@Nonnull
	@Override
	public CommandResult execute( @Nonnull final GuildMessageReceivedEvent event,  @Nonnull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		RSNAudioManager.shuffle(event.getGuild());
		Actions.reply(event, event.getAuthor().getAsMention() + " shuffled the queue");
		return CommandResult.SUCCESS;
	}
	
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.MODERATOR;
	}
	
	
	@Nonnull
	@Override
	public String getName(){
		return "Shuffle";
	}
	
	@Nonnull
	
	@Override
	public List<String> getCommandStrings(){
		return List.of("shuffle", "sh");
	}
	
	@Nonnull
	
	@Override
	public String getDescription(){
		return "Shuffle the queue";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
