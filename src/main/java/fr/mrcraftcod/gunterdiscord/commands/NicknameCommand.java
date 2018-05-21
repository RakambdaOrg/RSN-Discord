package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CallableCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Log;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.HierarchyException;
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
public class NicknameCommand extends BasicCommand
{
	@Override
	public String getCommandUsage(Guild guild)
	{
		return super.getCommandUsage(guild) + " <@utilisateur> [surnom]";
	}
	
	@Override
	public int getScope()
	{
		return ChannelType.TEXT.getId();
	}
	
	@Override
	public String getName()
	{
		return "Nickname";
	}
	
	@Override
	public List<String> getCommand()
	{
		return List.of("nickname", "nick");
	}
	
	@Override
	public String getDescription()
	{
		return "Change le surnom d'un utilisateur";
	}
	
	@SuppressWarnings("Duplicates")
	@Override
	public CommandResult execute(MessageReceivedEvent event, LinkedList<String> args) throws Exception
	{
		super.execute(event, args);
		if(event.getMessage().getMentionedUsers().size() > 0)
		{
			args.pop();
			Member member = event.getGuild().getMember(event.getMessage().getMentionedUsers().get(0));
			String oldName = member.getNickname();
			String newName;
			if(args.size() == 0)
				newName = null;
			else
				newName = args.stream().collect(Collectors.joining(" "));
			EmbedBuilder builder = new EmbedBuilder();
			builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
			builder.addField("Ancien surnom", oldName == null ? "*AUCUN*" : oldName, true);
			builder.addField("Nouveau surnom", newName == null ? "*AUCUN*" : newName, true);
			builder.addField("Utilisateur", member.getAsMention(), true);
			try
			{
				member.getGuild().getController().setNickname(member, newName).complete();
				builder.setColor(Color.GREEN);
				Log.info(Actions.getUserToLog(event.getAuthor()) + " renamed " + Actions.getUserToLog(member.getUser()) + " from `" + oldName + "` to `" + newName + "`");
			}
			catch(HierarchyException e)
			{
				builder.setColor(Color.RED);
				builder.setTitle("T'es cru changer le nom d'un mec plus haut que moi?!");
			}
			Actions.reply(event, builder.build());
		}
		else
		{
			EmbedBuilder builder = new EmbedBuilder();
			builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
			builder.setColor(Color.RED);
			builder.addField("Erreur", "Merci de mentionner un utilisateur", true);
			Actions.reply(event, builder.build());
		}
		return CommandResult.SUCCESS;
	}
	
	@Override
	public AccessLevel getAccessLevel()
	{
		return AccessLevel.MODERATOR;
	}
}
