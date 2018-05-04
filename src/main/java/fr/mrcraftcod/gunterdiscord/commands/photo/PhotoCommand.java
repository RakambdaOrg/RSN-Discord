package fr.mrcraftcod.gunterdiscord.commands.photo;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CallableCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.settings.configs.PhotoChannelConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.PhotoConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Log;
import fr.mrcraftcod.gunterdiscord.utils.Roles;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.io.File;
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
				Member member = event.getGuild().getMember(user);
				if(member == null || !Utilities.hasRole(member, Roles.TROMBINOSCOPE))
					Actions.reply(event, "L'utilisateur ne fait pas parti du trombinoscope");
				else if(event.getTextChannel().equals(new PhotoChannelConfig().getTextChannel(event.getJDA())))
				{
					List<String> paths = new PhotoConfig().getValue(user.getIdLong());
					if(paths != null && !paths.isEmpty())
					{
						boolean randomGen = true;
						int rnd = ThreadLocalRandom.current().nextInt(paths.size());
						if(args.peek() != null)
							try
							{
								rnd = Math.max(0, Math.min(paths.size(), Integer.parseInt(args.pop())) - 1);
								randomGen = false;
							}
							catch(Exception e)
							{
								Log.warning("Provided photo index isn't an integer", e);
							}
						File file = new File(paths.get(rnd));
						if(file.exists())
						{
							String ID = file.getName().substring(0, file.getName().lastIndexOf("."));
							Actions.reply(event, "%s a demandé la photo (%d/%d%s) de %s (ID: %s)", event.getAuthor().getAsMention(), rnd + 1, paths.size(), randomGen ? " aléatoire" : "", member.getNickname(), ID);
							event.getTextChannel().sendFile(file).queue();
						}
						else
							Actions.reply(event, "Désolé je ne retrouves plus l'image");
					}
					else
						Actions.reply(event, "Cet utilisateur n'a pas d'image");
				}
			}
		}
		else
			Actions.reply(event, "Participants du trombinoscope: %s", Utilities.getMembersRole(event.getGuild(), Roles.TROMBINOSCOPE).stream().map(u -> u.getUser().getName()).collect(Collectors.joining(", ")));
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
