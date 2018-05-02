package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.settings.NoValueDefinedException;
import fr.mrcraftcod.gunterdiscord.settings.configs.PhotoChannelConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.PhotoConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
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
public class PhotoCommand extends BasicCommand
{
	@SuppressWarnings("Duplicates")
	@Override
	public CommandResult execute(MessageReceivedEvent event, LinkedList<String> args) throws Exception
	{
		if(super.execute(event, args) == CommandResult.NOT_ALLOWED)
			return CommandResult.NOT_ALLOWED;
		return handlePublic(event, args);
	}
	
	@SuppressWarnings("Duplicates")
	private CommandResult handlePublic(MessageReceivedEvent event, LinkedList<String> args)
	{
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
			{
				Actions.sendMessage(event.getAuthor().openPrivateChannel().complete(), "Utilisateur non trouvé");
			}
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
									e.printStackTrace();
								}
							File file = new File(paths.get(rnd));
							if(file.exists())
							{
								String ID = file.getName().substring(0, file.getName().lastIndexOf("."));
								event.getTextChannel().sendMessage(event.getAuthor().getAsMention() + " a demandé la photo (" + (rnd + 1) + "/" + paths.size() + ") de " + user.getName() + " (ID: " + ID + ")").queue();
								event.getTextChannel().sendFile(file).queue();
							}
							else
								Actions.sendMessage(event.getAuthor().openPrivateChannel().complete(), "Désolé je ne retrouves plus l'image");
						}
						else
							Actions.sendMessage(event.getAuthor().openPrivateChannel().complete(), "Cet utilisateur n'a pas d'image");
					}
				}
				catch(InvalidClassException | NoValueDefinedException e)
				{
					e.printStackTrace();
				}
			}
		}
		else
		{
			try
			{
				if(new PhotoChannelConfig().getLong() == event.getTextChannel().getIdLong())
				{
					event.getTextChannel().sendMessage("Participants: " + event.getGuild().getMembersWithRoles(Utilities.getRole(event.getJDA(), "Trombi")).stream().map(u -> u.getUser().getName()).collect(Collectors.joining(", "))).queue();
				}
			}
			catch(InvalidClassException | NoValueDefinedException e)
			{
				e.printStackTrace();
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
