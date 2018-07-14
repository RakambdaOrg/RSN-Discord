package fr.mrcraftcod.gunterdiscord.commands.warn;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.settings.configs.DoubleWarnRoleConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.DoubleWarnTimeConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.RemoveRoleConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
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
public class DoubleWarnCommand extends BasicCommand
{
	@Override
	public void addHelp(Guild guild, EmbedBuilder builder)
	{
		super.addHelp(guild, builder);
		builder.addField("Utilisateur", "L'utilisateur à warn", false);
	}
	
	@Override
	public CommandResult execute(MessageReceivedEvent event, LinkedList<String> args) throws Exception
	{
		super.execute(event, args);
		if(event.getMessage().getMentionedUsers().size() > 0)
		{
			User user = event.getMessage().getMentionedUsers().get(0);
			Role role = new DoubleWarnRoleConfig().getRole(event.getGuild());
			EmbedBuilder builder = new EmbedBuilder();
			builder.setAuthor(user.getName(), null, user.getAvatarUrl());
			if(role == null)
			{
				builder.setColor(Color.RED);
				builder.addField("Erreur", "Merci de configurer le role à donner", true);
			}
			else
			{
				Actions.giveRole(event.getGuild(), user, role);
				new RemoveRoleConfig().addValue(event.getGuild(), user.getIdLong(), role.getIdLong(), System.currentTimeMillis() + new DoubleWarnTimeConfig().getLong(event.getGuild(), 1) * 24 * 60 * 60 * 1000L);
				builder.setColor(Color.GREEN);
				builder.addField("Congratulations", user.getAsMention() + " à rejoint le role " + role.getAsMention() + " pour une durée de 2 semaines", false);
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
	public String getCommandUsage()
	{
		return super.getCommandUsage() + " <@utilisateur>";
	}
	
	@Override
	public AccessLevel getAccessLevel()
	{
		return AccessLevel.MODERATOR;
	}
	
	@Override
	public String getName()
	{
		return "Warn";
	}
	
	@Override
	public List<String> getCommand()
	{
		return List.of("doublewarn", "dwarn");
	}
	
	@Override
	public String getDescription()
	{
		return "Warn un utilisateur (en donnant un role) pendant 2 semaines";
	}
	
	@Override
	public int getScope()
	{
		return ChannelType.TEXT.getId();
	}
}
