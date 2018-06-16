package fr.mrcraftcod.gunterdiscord.commands.photo;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.settings.configs.PhotoChannelConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.PhotoConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.TrombinoscopeRoleConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Log;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
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
public class PhotoGetCommand extends BasicCommand
{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	PhotoGetCommand(Command parent)
	{
		super(parent);
	}
	
	@Override
	public List<String> getCommand()
	{
		return List.of("photo", "p", "g", "get");
	}
	
	@Override
	public String getCommandUsage()
	{
		return super.getCommandUsage() + " [@utilisateur] [numéro]";
	}
	
	@SuppressWarnings("Duplicates")
	@Override
	public CommandResult execute(MessageReceivedEvent event, LinkedList<String> args) throws Exception
	{
		Actions.deleteMessage(event.getMessage());
		if(super.execute(event, args) == CommandResult.NOT_ALLOWED)
			return CommandResult.NOT_ALLOWED;
		User user;
		List<User> users = event.getMessage().getMentionedUsers();
		if(users.size() > 0)
		{
			user = users.get(0);
			args.poll();
		}
		else
			user = event.getAuthor();
		
		Member member = event.getGuild().getMember(user);
		if(member == null || !Utilities.hasRole(member, new TrombinoscopeRoleConfig().getRole(event.getGuild())))
		{
			EmbedBuilder builder = new EmbedBuilder();
			builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
			builder.setColor(Color.RED);
			builder.setTitle("L'utilisateur ne fait pas parti du trombinoscope");
			Actions.reply(event, builder.build());
		}
		else if(new PhotoChannelConfig().isChannel(event.getTextChannel()))
		{
			List<String> paths = new PhotoConfig().getValue(event.getGuild(), user.getIdLong());
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
						Log.warning(e, "Provided photo index isn't an integer");
					}
				File file = new File(paths.get(rnd));
				if(file.exists())
				{
					String ID = file.getName().substring(0, file.getName().lastIndexOf("."));
					EmbedBuilder builder = new EmbedBuilder();
					builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
					builder.setColor(Color.GREEN);
					builder.addField("Sélection", String.format("%d/%d%s", rnd + 1, paths.size(), randomGen ? " aléatoire" : ""), true);
					builder.addField("Utilisateur", user.getAsMention(), true);
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
		return CommandResult.SUCCESS;
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
	public void addHelp(Guild guild, EmbedBuilder builder)
	{
		super.addHelp(guild, builder);
		builder.addField("Optionnel: Utilisateur", "L'utilisateur dont on veut la photo (par défaut @me)", false);
		builder.addField("Optionnel: Numéro", "Le numéro de la photo à tirer (par défaut aléatoire)", false);
	}
}
