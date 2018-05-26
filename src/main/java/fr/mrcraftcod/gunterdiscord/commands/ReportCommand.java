package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.settings.configs.ReportChannelConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 13/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-13
 */
public class ReportCommand extends BasicCommand
{
	@Override
	public String getCommandUsage()
	{
		return super.getCommandUsage() + " raison...";
	}
	
	@Override
	public String getName()
	{
		return "Report";
	}
	
	@Override
	public List<String> getCommand()
	{
		return List.of("report", "r");
	}
	
	@Override
	public CommandResult execute(MessageReceivedEvent event, LinkedList<String> args) throws Exception
	{
		super.execute(event, args);
		TextChannel channel = new ReportChannelConfig().getTextChannel(event.getGuild());
		if(channel == null)
			return CommandResult.FAILED;
		else
		{
			EmbedBuilder builder = new EmbedBuilder();
			builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
			builder.setColor(Color.ORANGE);
			builder.setTitle("Nouveau report");
			builder.addField("Utilisateur", event.getAuthor().getAsMention(), false);
			builder.addField("Raison", args.stream().collect(Collectors.joining(" ")), false);
			builder.setTimestamp(event.getMessage().getCreationTime());
			Actions.sendMessage(channel, builder.build());
			Actions.replyPrivate(event.getAuthor(), "Votre message a bien été transféré. Merci à vous.");
		}
		return CommandResult.SUCCESS;
	}
	
	@Override
	public void addHelp(Guild guild, EmbedBuilder builder)
	{
		super.addHelp(guild, builder);
		builder.addField("Raison", "La raison du report", false);
	}
	
	@Override
	public String getDescription()
	{
		return "Envoi un message aux modérateurs";
	}
	
	@Override
	public int getScope()
	{
		return ChannelType.TEXT.getId();
	}
	
	@Override
	public AccessLevel getAccessLevel()
	{
		return AccessLevel.ALL;
	}
}
