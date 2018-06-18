package fr.mrcraftcod.gunterdiscord.commands.photo;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.settings.configs.PhotoChannelConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.PhotoConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.TrombinoscopeRoleConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.awt.*;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 13/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-13
 */
public class PhotoAddCommand extends BasicCommand
{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	PhotoAddCommand(Command parent)
	{
		super(parent);
	}
	
	@Override
	public String getCommandUsage()
	{
		return super.getCommandUsage() + " [@utilisateur]";
	}
	
	@SuppressWarnings("Duplicates")
	@Override
	public CommandResult execute(MessageReceivedEvent event, LinkedList<String> args) throws Exception
	{
		Actions.deleteMessage(event.getMessage());
		if(super.execute(event, args) == CommandResult.NOT_ALLOWED)
			return CommandResult.NOT_ALLOWED;
		if(event.getMessage().getAttachments().size() > 0)
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
			
			if(user.getIdLong() != event.getAuthor().getIdLong() && !Utilities.isAdmin(event.getMember()))
				Actions.replyPrivate(event.getAuthor(), "Vous ne pouvez pas ajouter une image pour quelqu'un d'autre");
			else
			{
				Message.Attachment attachment = event.getMessage().getAttachments().get(0);
				String ext = attachment.getFileName().substring(attachment.getFileName().lastIndexOf("."));
				File saveFile = new File("./pictures/" + user.getIdLong() + "/", event.getMessage().getCreationTime().toEpochSecond() + ext);
				//noinspection ResultOfMethodCallIgnored
				saveFile.getParentFile().mkdirs();
				if(attachment.download(saveFile) && attachment.getSize() == saveFile.length())
				{
					new PhotoConfig().addValue(event.getGuild(), user.getIdLong(), saveFile.getPath());
					Actions.giveRole(event.getGuild(), user, new TrombinoscopeRoleConfig().getRole(event.getGuild()));
					EmbedBuilder builder = new EmbedBuilder();
					builder.setAuthor(user.getName(), null, user.getAvatarUrl());
					builder.setColor(Color.GREEN);
					builder.setTitle("Nouvelle photo");
					builder.addField("Utilisateur", user.getAsMention(), true);
					builder.addField("ID", "" + event.getMessage().getCreationTime().toEpochSecond(), true);
					Actions.sendMessage(new PhotoChannelConfig().getTextChannel(event.getGuild()), builder.build());
				}
				else
				{
					Actions.replyPrivate(event.getAuthor(), "L'envoi a échoué");
					if(saveFile.exists())
						//noinspection ResultOfMethodCallIgnored
						saveFile.delete();
				}
			}
		}
		else
			Actions.replyPrivate(event.getAuthor(), "Veuillez joindre une image");
		return CommandResult.SUCCESS;
	}
	
	@Override
	public void addHelp(Guild guild, EmbedBuilder builder)
	{
		super.addHelp(guild, builder);
		builder.addField("Optionnel: Utilisateur", "L'utilisateur représenté par la photo (par défaut @me)", false);
	}
	
	@Override
	public int getScope()
	{
		return ChannelType.TEXT.getId();
	}
	
	@Override
	public String getName()
	{
		return "Ajouter photo";
	}
	
	@Override
	public List<String> getCommand()
	{
		return List.of("add", "a");
	}
	
	@Override
	public String getDescription()
	{
		return "Ajoute une photo au trombinoscope";
	}
	
	@Override
	public AccessLevel getAccessLevel()
	{
		return AccessLevel.ALL;
	}
}
