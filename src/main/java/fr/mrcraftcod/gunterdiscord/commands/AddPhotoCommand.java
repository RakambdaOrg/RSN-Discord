package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.settings.NoValueDefinedException;
import fr.mrcraftcod.gunterdiscord.settings.configs.PhotoChannelConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.PhotoConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.io.File;
import java.io.InvalidClassException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 13/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-13
 */
public class AddPhotoCommand extends BasicCommand
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
		if(event.getMessage().getAttachments().size() > 0)
		{
			User user = null;
			if(args.size() > 0)
			{
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
			}
			else
				user = event.getAuthor();
			if(Utilities.isAdmin(event.getMember()))
			{
				Message.Attachment attachment = event.getMessage().getAttachments().get(0);
				String ext = attachment.getFileName().substring(attachment.getFileName().lastIndexOf("."));
				File saveFile = new File("./pictures/" + user.getIdLong() + "/", event.getMessage().getCreationTime().toEpochSecond() + ext);
				saveFile.getParentFile().mkdirs();
				if(attachment.download(saveFile))
				{
					new PhotoConfig().addValue(user.getIdLong(), saveFile.getAbsolutePath());
					event.getGuild().getController().addRolesToMember(event.getGuild().getMember(user), Utilities.getRole(event.getJDA(), "Trombi")).queue();
					TextChannel chan = null;
					try
					{
						chan = event.getGuild().getTextChannelById(new PhotoChannelConfig().getLong());
					}
					catch(InvalidClassException | NoValueDefinedException e)
					{
						e.printStackTrace();
					}
					if(chan == null)
						chan = event.getTextChannel();
					Actions.sendMessage(chan, "Photo ajoutée pour " + user.getAsMention() + " (ID: " + event.getMessage().getCreationTime().toEpochSecond() + ")");
				}
				else
					Actions.sendMessage(event.getPrivateChannel(), "L'envoi a échoué");
			}
		}
		else
		{
			Actions.sendMessage(event.getAuthor().openPrivateChannel().complete(), "Veuillez joindre un image");
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
		return "Ajouter photo";
	}
	
	@Override
	public String getCommand()
	{
		return "addPhoto";
	}
}
