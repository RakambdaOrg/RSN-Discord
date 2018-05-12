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
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.awt.*;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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
				{
					EmbedBuilder builder = new EmbedBuilder();
					builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
					builder.setColor(Color.RED);
					builder.setTitle("L'utilisateur ne fait pas parti du trombinoscope");
					Actions.reply(event, builder.build());
				}
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
							EmbedBuilder builder = new EmbedBuilder();
							builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
							builder.setColor(Color.GREEN);
							builder.addField("Sélection", String.format("%d/%d%s", rnd + 1, paths.size(), randomGen ? " aléatoire" : ""), true);
							builder.addField("Utilisateur", user.getName(), true);
							builder.addField("ID", ID, true);
							Actions.reply(event, builder.build());
							Actions.sendFile(event.getTextChannel(), file);
						}
						else
						{
							EmbedBuilder builder = new EmbedBuilder();
							builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
							builder.setColor(Color.RED);
							builder.setTitle("Image non trouvée");
							Actions.reply(event, builder.build());
						}
					}
					else
					{
						EmbedBuilder builder = new EmbedBuilder();
						builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
						builder.setColor(Color.ORANGE);
						builder.setTitle("Cet utilisateur n'a pas d'images");
						Actions.reply(event, builder.build());
					}
				}
			}
		}
		else
		{
			EmbedBuilder builder = new EmbedBuilder();
			builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
			builder.setColor(Color.GREEN);
			builder.setTitle("Participants du trombinoscope");
			Utilities.getMembersRole(event.getGuild(), Roles.TROMBINOSCOPE).stream().map(u -> u.getUser().getName()).forEach(u -> builder.addField("", u, false));
			Actions.reply(event, builder.build());
		}
		return CommandResult.SUCCESS;
	}
	
	@Override
	public String getCommandUsage()
	{
		return super.getCommandUsage() + " [utilisateur] [numéro]";
	}
	
	@Override
	public String getDescription()
	{
		return "Obtient une photo du trombinoscope";
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
		return "Photo";
	}
	
	@Override
	public List<String> getCommand()
	{
		return List.of("photo", "p");
	}
}
