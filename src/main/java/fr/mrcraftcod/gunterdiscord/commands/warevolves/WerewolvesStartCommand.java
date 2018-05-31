package fr.mrcraftcod.gunterdiscord.commands.warevolves;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.listeners.WerewolvesListener;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-05-31
 */
public class WerewolvesStartCommand extends BasicCommand
{
	public WerewolvesStartCommand(Command parent)
	{
		super(parent);
	}
	
	@Override
	public CommandResult execute(MessageReceivedEvent event, LinkedList<String> args) throws Exception
	{
		super.execute(event, args);
		if(event.getMember().getVoiceState().inVoiceChannel())
		{
			WerewolvesListener.getGame(event.getMember().getVoiceState().getChannel()).ifPresentOrElse(g -> {}, () -> Actions.reply(event, "Impossible de créer la partie, êtes vous bien au moins 5 joueurs dans le vocal?"));
		}
		else
			Actions.reply(event, Utilities.buildEmbed(event.getAuthor(), Color.RED, "Erreur").addField("Raison", "Vous devez être dans un channel vocal pour pouvoir démarer une partie", false).build());
		return CommandResult.SUCCESS;
	}
	
	@Override
	public int getScope()
	{
		return ChannelType.TEXT.getId();
	}
	
	@Override
	public String getName()
	{
		return "Commence une partie de loups garous";
	}
	
	@Override
	public List<String> getCommand()
	{
		return List.of("start");
	}
	
	@Override
	public String getDescription()
	{
		return "Commence une partie de loups garous";
	}
	
	@Override
	public AccessLevel getAccessLevel()
	{
		return AccessLevel.ALL;
	}
}
