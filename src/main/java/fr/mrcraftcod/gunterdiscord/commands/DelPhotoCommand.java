package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.settings.configs.PhotoConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
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
public class DelPhotoCommand extends BasicCommand
{
	@SuppressWarnings("Duplicates")
	@Override
	public CommandResult execute(MessageReceivedEvent event, LinkedList<String> args) throws Exception
	{
		if(super.execute(event, args) == CommandResult.NOT_ALLOWED)
			return CommandResult.NOT_ALLOWED;
		switch(event.getChannel().getType())
		{
			case PRIVATE:
				return handlePrivate(event, args);
			case TEXT:
				return handlePublic(event, args);
			default:
				return CommandResult.FAILED;
		}
	}
	
	@SuppressWarnings("Duplicates")
	private CommandResult handlePublic(MessageReceivedEvent event, LinkedList<String> args)
	{
		if(args.size() > 0)
		{
			User user = null;
			try
			{
				user = event.getJDA().getUserById(Long.parseLong(args.peek()));
			}
			catch(NumberFormatException e)
			{
				
				List<User> users = event.getMessage().getMentionedUsers();
				if(users.size() > 0)
					user = users.get(0);
			}
			if(user == null)
			{
				Actions.sendMessage(event.getAuthor().openPrivateChannel().complete(), "Utilisateur non trouvé");
			}
			else
			{
				if(user.getIdLong() != event.getAuthor().getIdLong())
				{
					if(Utilities.isModerator(event.getMember()) || Utilities.isAdmin(event.getMember()))
					{
						new PhotoConfig().deleteKey(user.getIdLong());
						event.getGuild().getController().removeRolesFromMember(event.getGuild().getMember(user), Utilities.getRole(event.getJDA(), "Trombi")).complete();
						Actions.sendMessage(event.getAuthor().openPrivateChannel().complete(), "Photo de " + user.getAsMention() + " supprimée");
					}
					else
						Actions.sendMessage(event.getAuthor().openPrivateChannel().complete(), "Vous ne pouvez pas supprimer la photo de quelqu'un d'autre");
				}
				else
				{
					new PhotoConfig().deleteKey(event.getAuthor().getIdLong());
					event.getGuild().getController().removeRolesFromMember(event.getMember(), Utilities.getRole(event.getJDA(), "Trombi")).complete();
					Actions.sendMessage(event.getAuthor().openPrivateChannel().complete(), "Photo supprimée");
				}
			}
		}
		else
		{
			new PhotoConfig().deleteKey(event.getAuthor().getIdLong());
			event.getGuild().getController().removeRolesFromMember(event.getMember(), Utilities.getRole(event.getJDA(), "Trombi")).complete();
			Actions.sendMessage(event.getAuthor().openPrivateChannel().complete(), "Photo supprimée");
		}
		event.getMessage().delete().complete();
		return CommandResult.SUCCESS;
	}
	
	private CommandResult handlePrivate(MessageReceivedEvent event, LinkedList<String> args)
	{
		new PhotoConfig().deleteKey(event.getAuthor().getIdLong());
		Actions.sendMessage(event.getPrivateChannel(), "Photo supprimée");
		return CommandResult.SUCCESS;
	}
	
	@Override
	public String getCommandDescription()
	{
		return super.getCommandDescription() + " <user>";
	}
	
	@Override
	protected AccessLevel getAccessLevel()
	{
		return AccessLevel.ALL;
	}
	
	@Override
	public int getScope()
	{
		return -5;
	}
	
	@Override
	public String getName()
	{
		return "Photo";
	}
	
	@Override
	public String getCommand()
	{
		return "photo";
	}
}
