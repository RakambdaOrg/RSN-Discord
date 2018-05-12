package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CallableCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.listeners.CommandsMessageListener;
import fr.mrcraftcod.gunterdiscord.settings.configs.PrefixConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.awt.*;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-12
 */
@CallableCommand
public class HelpCommand extends BasicCommand
{
	@Override
	public int getScope()
	{
		return ChannelType.TEXT.getId();
	}
	
	@Override
	public String getName()
	{
		return "Help";
	}
	
	@Override
	public List<String> getCommand()
	{
		return List.of("help", "h");
	}
	
	@Override
	public String getCommandUsage()
	{
		return super.getCommandUsage() + " [commande]";
	}
	
	@Override
	public String getDescription()
	{
		return "Aide des commandes";
	}
	
	@Override
	public CommandResult execute(MessageReceivedEvent event, LinkedList<String> args) throws Exception
	{
		super.execute(event, args);
		String prefix = new PrefixConfig().getString("");
		if(args.size() < 1)
		{
			EmbedBuilder builder = new EmbedBuilder();
			builder.setColor(Color.GREEN);
			builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
			builder.setTitle("Commandes disponibles");
			CommandsMessageListener.commands.stream().filter(c -> c.isAllowed(event.getMember())).map(s -> new MessageEmbed.Field(prefix + s.getCommand().get(0), s.getDescription(), false)).sorted(Comparator.comparing(MessageEmbed.Field::getName)).forEach(builder::addField);
			Actions.reply(event, builder.build());
		}
		else
		{
			Command command = CommandsMessageListener.commands.stream().filter(s -> s.getCommand().contains(args.get(0).toLowerCase())).filter(c -> c.isAllowed(event.getMember())).findAny().orElse(null);
			EmbedBuilder builder = new EmbedBuilder();
			builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
			if(command != null)
			{
				builder.setColor(Color.GREEN);
				builder.setTitle("Commande " + prefix + command.getCommand().get(0));
				builder.addField("Description", command.getDescription(), false);
				builder.addField("Utilisation", command.getCommandUsage(), false);
			}
			else
			{
				builder.setColor(Color.ORANGE);
				builder.addField(prefix + args.get(0), "Cette commande n'existe pas ou vous n'y avez pas accès", false);
			}
			Actions.reply(event, builder.build());
		}
		
		return CommandResult.SUCCESS;
	}
	
	@Override
	public AccessLevel getAccessLevel()
	{
		return AccessLevel.ADMIN;
	}
}
