package fr.mrcraftcod.gunterdiscord.commands.photo;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CallableCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.settings.NoValueDefinedException;
import fr.mrcraftcod.gunterdiscord.settings.configs.PhotoChannelConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.PhotoConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Log;
import fr.mrcraftcod.gunterdiscord.utils.Roles;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.io.File;
import java.io.InvalidClassException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 13/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-13
 */
@CallableCommand
public class PhotoCommand extends BasicCommand
{
	@SuppressWarnings("Duplicates")
	@Override
	public CommandResult execute(MessageReceivedEvent event, LinkedList<String> args) throws Exception
	{
		if(super.execute(event, args) == CommandResult.NOT_ALLOWED)
			return CommandResult.NOT_ALLOWED;
		if(args.size() > 0)
		{
			User user = null;
			try
			{
				user = event.getJDA().getUserById(Long.parseLong(args.pop()));
			}
			catch(Exception e)
			{
				List<User> users = event.getMessage().getMentionedUsers();
				if(users.size() > 0)
					user = users.get(0);
			}
			
			if(user == null)
				Actions.replyPrivate(event.getAuthor(), "Utilisateur non trouvé");
			else
			{
				try
				{
					if(new PhotoChannelConfig().getLong() == event.getTextChannel().getIdLong())
					{
						List<String> paths = new PhotoConfig().getValue(user.getIdLong());
						if(paths != null && !paths.isEmpty())
						{
							int rnd = ThreadLocalRandom.current().nextInt(paths.size());
							if(args.peek() != null)
								try
								{
									rnd = Math.max(0, Math.min(paths.size(), Integer.parseInt(args.pop())) - 1);
								}
								catch(Exception e)
								{
									Log.warning("Provided argument isn't an integer", e);
								}
							File file = new File(paths.get(rnd));
							if(file.exists())
							{
								String ID = file.getName().substring(0, file.getName().lastIndexOf("."));
								Actions.reply(event, event.getAuthor().getAsMention() + " a demandé la photo (" + (rnd + 1) + "/" + paths.size() + ") de " + user.getName() + " (ID: " + ID + ")");
								event.getTextChannel().sendFile(file).queue();
							}
							else
								Actions.reply(event, "Désolé je ne retrouves plus l'image");
						}
						else
							Actions.reply(event, "Cet utilisateur n'a pas d'image");
					}
				}
				catch(InvalidClassException | NoValueDefinedException e)
				{
					Log.error("Couldn't get photo channel", e);
				}
			}
		}
		else
		{
			try
			{
				if(new PhotoChannelConfig().getLong() == event.getTextChannel().getIdLong())
				{
					Actions.reply(event, "Participants: " + event.getGuild().getMembersWithRoles(Utilities.getRole(event.getJDA(), Roles.TROMBINOSCOPE)).stream().map(u -> u.getUser().getName()).collect(Collectors.joining(", ")));
				}
			}
			catch(InvalidClassException | NoValueDefinedException e)
			{
				Log.error("Couldn't get photo channel", e);
			}
		}
		event.getMessage().delete().queue();
		return CommandResult.SUCCESS;
	}
	
	@Override
	public String getCommandDescription()
	{
		return super.getCommandDescription() + " [user]";
	}
	
	@Override
	protected AccessLevel getAccessLevel()
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
		return "Photo";
	}
	
	@Override
	public String getCommand()
	{
		return "photo";
	}
}
