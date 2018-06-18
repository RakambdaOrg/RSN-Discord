package fr.mrcraftcod.gunterdiscord.commands.photo;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.settings.configs.PhotoConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.TrombinoscopeRoleConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 13/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-13
 */
public class PhotoDeleteCommand extends BasicCommand
{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	PhotoDeleteCommand(Command parent)
	{
		super(parent);
	}
	
	@Override
	public String getCommandUsage()
	{
		return super.getCommandUsage() + " [@utilisateur] [ID]";
	}
	
	@SuppressWarnings("Duplicates")
	@Override
	public CommandResult execute(MessageReceivedEvent event, LinkedList<String> args) throws Exception
	{
		Actions.deleteMessage(event.getMessage());
		if(super.execute(event, args) == CommandResult.NOT_ALLOWED)
			return CommandResult.NOT_ALLOWED;
		if(args.size() > 0)
		{
			User user;
			List<User> users = event.getMessage().getMentionedUsers();
			if(users.size() > 0)
			{
				user = users.get(0);
				args.poll();
			}
			else
				user = event.getAuthor();
			
			if(user.getIdLong() != event.getAuthor().getIdLong())
			{
				if(Utilities.isModerator(event.getMember()) || Utilities.isAdmin(event.getMember()))
				{
					new PhotoConfig().deleteKeyValue(event.getGuild(), user.getIdLong(), args.poll());
					if(new PhotoConfig().getValue(event.getGuild(), user.getIdLong()).isEmpty())
						Actions.removeRole(event.getGuild(), user, new TrombinoscopeRoleConfig().getRole(event.getGuild()));
					Actions.replyPrivate(event.getAuthor(), "Photos de %s supprimées", user.getAsMention());
				}
				else
					Actions.replyPrivate(event.getAuthor(), "Vous ne pouvez pas supprimer la photo de quelqu'un d'autre");
			}
			else
			{
				new PhotoConfig().deleteKeyValue(event.getGuild(), user.getIdLong(), args.poll());
				if(new PhotoConfig().getValue(event.getGuild(), user.getIdLong()).isEmpty())
					Actions.removeRole(event.getGuild(), user, new TrombinoscopeRoleConfig().getRole(event.getGuild()));
				Actions.replyPrivate(event.getAuthor(), "Photos supprimées");
			}
		}
		else
		{
			new PhotoConfig().deleteKey(event.getGuild(), event.getAuthor().getIdLong());
			Actions.removeRole(event.getGuild(), event.getAuthor(), new TrombinoscopeRoleConfig().getRole(event.getGuild()));
			Actions.replyPrivate(event.getAuthor(), "Photos supprimées");
		}
		return CommandResult.SUCCESS;
	}
	
	@Override
	public void addHelp(Guild guild, EmbedBuilder builder)
	{
		super.addHelp(guild, builder);
		builder.addField("Optionnel: Utilisateur", "L'utilisateur visé par la suppression (par défaut @me)", false);
		builder.addField("Optionnel: ID", "L'ID de la photo à supprimer (si aucun n'est précisé toutes les photos seront supprimées)", false);
	}
	
	@Override
	public int getScope()
	{
		return ChannelType.TEXT.getId();
	}
	
	@Override
	public String getName()
	{
		return "Supprimer photo";
	}
	
	@Override
	public List<String> getCommand()
	{
		return List.of("del", "d", "rm", "s");
	}
	
	@Override
	public String getDescription()
	{
		return "Supprime une ou des photos du trombinoscope";
	}
	
	@Override
	public AccessLevel getAccessLevel()
	{
		return AccessLevel.ALL;
	}
}
