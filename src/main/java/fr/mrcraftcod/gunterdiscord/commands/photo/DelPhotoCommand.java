package fr.mrcraftcod.gunterdiscord.commands.photo;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CallableCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.settings.configs.PhotoConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Roles;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.core.entities.ChannelType;
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
@CallableCommand
public class DelPhotoCommand extends BasicCommand
{
	@SuppressWarnings("Duplicates")
	@Override
	public CommandResult execute(MessageReceivedEvent event, LinkedList<String> args) throws Exception
	{
		Actions.deleteMessage(event.getMessage());
		if(super.execute(event, args) == CommandResult.NOT_ALLOWED)
			return CommandResult.NOT_ALLOWED;
		if(args.size() > 0)
		{
			User user = null;
			try
			{
				user = event.getJDA().getUserById(Long.parseLong(args.pop()));
			}
			catch(NumberFormatException e)
			{
				
				List<User> users = event.getMessage().getMentionedUsers();
				if(users.size() > 0)
					user = users.get(0);
			}
			if(user == null)
				Actions.replyPrivate(event.getAuthor(), "Utilisateur non trouvé");
			else
			{
				if(user.getIdLong() != event.getAuthor().getIdLong())
				{
					if(Utilities.isModerator(event.getMember()) || Utilities.isAdmin(event.getMember()))
					{
						new PhotoConfig().deleteKeyValue(user.getIdLong(), args.poll());
						if(new PhotoConfig().getValue(user.getIdLong()).isEmpty())
							Actions.removeRole(event.getGuild(), user, Roles.TROMBINOSCOPE);
						Actions.replyPrivate(event.getAuthor(), "Photos de %s supprimées", user.getAsMention());
					}
					else
						Actions.replyPrivate(event.getAuthor(), "Vous ne pouvez pas supprimer la photo de quelqu'un d'autre");
				}
				else
				{
					new PhotoConfig().deleteKeyValue(user.getIdLong(), args.poll());
					if(new PhotoConfig().getValue(user.getIdLong()).isEmpty())
						Actions.removeRole(event.getGuild(), user, Roles.TROMBINOSCOPE);
					Actions.replyPrivate(event.getAuthor(), "Photos supprimées");
				}
			}
		}
		else
		{
			new PhotoConfig().deleteKey(event.getAuthor().getIdLong());
			Actions.removeRole(event.getGuild(), event.getAuthor(), Roles.TROMBINOSCOPE);
			Actions.replyPrivate(event.getAuthor(), "Photos supprimées");
		}
		return CommandResult.SUCCESS;
	}
	
	@Override
	public String getCommandUsage()
	{
		return super.getCommandUsage() + " [utilisateur] [ID]";
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
		return List.of("delphoto", "dp");
	}
}
