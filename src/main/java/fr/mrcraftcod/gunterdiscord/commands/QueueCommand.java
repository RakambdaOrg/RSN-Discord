package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CallableCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.settings.configs.QueueChannelConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-12
 */
@CallableCommand
public class QueueCommand extends BasicCommand
{
	@Override
	public CommandResult execute(MessageReceivedEvent event, LinkedList<String> args) throws Exception
	{
		super.execute(event, args);
		
		EmbedBuilder builder = new EmbedBuilder();
		builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
		builder.setColor(Color.GREEN);
		builder.setTitle("Nouvelle entrée");
		builder.addField("Utilisateur", event.getAuthor().getAsMention(), true);
		builder.addField("Message", args.stream().collect(Collectors.joining(" ")), false);
		
		Actions.sendMessage(new QueueChannelConfig().getTextChannel(event.getGuild()), builder.build());
		return CommandResult.SUCCESS;
	}
	
	@Override
	public String getCommandUsage(Guild guild)
	{
		return super.getCommandUsage(guild) + " <message>";
	}
	
	@Override
	public int getScope()
	{
		return ChannelType.TEXT.getId();
	}
	
	@Override
	public String getName()
	{
		return "Queue";
	}
	
	@Override
	public List<String> getCommand()
	{
		return List.of("queue");
	}
	
	@Override
	public String getDescription()
	{
		return "S'enregistre dans la file d'attente";
	}
	
	@Override
	public AccessLevel getAccessLevel()
	{
		return AccessLevel.ALL;
	}
}
