package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
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

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-12
 */
public class QueueCommand extends BasicCommand
{
	@Override
	public String getCommandUsage()
	{
		return super.getCommandUsage() + " message...";
	}
	
	@Override
	public CommandResult execute(MessageReceivedEvent event, LinkedList<String> args) throws Exception
	{
		super.execute(event, args);
		
		if(args.size() == 0)
			Actions.replyPrivate(event.getAuthor(), "Merci de renseigner une raison pour rentrer dans la queue");
		else if(args.peek().equalsIgnoreCase("message"))
			Actions.replyPrivate(event.getAuthor(), "Merci de renseigner une raison pour rentrer dans la queue, et pas 'message'");
		else
		{
			EmbedBuilder builder = new EmbedBuilder();
			builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
			builder.setColor(Color.GREEN);
			builder.setTitle("Nouvelle entrée");
			builder.addField("Utilisateur", event.getAuthor().getAsMention(), true);
			builder.addField("Message", String.join(" ", args), false);
			
			Actions.sendMessage(new QueueChannelConfig().getTextChannel(event.getGuild()), builder.build());
			Actions.replyPrivate(event.getAuthor(), "Ok, ta candidature a été prise en compte");
		}
		return CommandResult.SUCCESS;
	}
	
	@Override
	public void addHelp(Guild guild, EmbedBuilder builder)
	{
		super.addHelp(guild, builder);
		builder.addField("Message", "La raison pour laquelle vous rejoignez la liste d'attente", false);
	}
	
	@Override
	public int getScope()
	{
		return ChannelType.TEXT.getId();
	}
	
	@Override
	public String getName()
	{
		return "File d'attente";
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
