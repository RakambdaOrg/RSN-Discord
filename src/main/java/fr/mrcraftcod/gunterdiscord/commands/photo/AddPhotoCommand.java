package fr.mrcraftcod.gunterdiscord.commands.photo;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CallableCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.settings.configs.PhotoChannelConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.PhotoConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Roles;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 13/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-13
 */
@CallableCommand
public class AddPhotoCommand extends BasicCommand
{
	@SuppressWarnings("Duplicates")
	@Override
	public CommandResult execute(MessageReceivedEvent event, LinkedList<String> args) throws Exception
	{
		Actions.deleteMessage(event.getMessage());
		if(super.execute(event, args) == CommandResult.NOT_ALLOWED)
			return CommandResult.NOT_ALLOWED;
		if(Utilities.hasRole(event.getMember(), Roles.TROMBINOSCOPE) || Utilities.isAdmin(event.getMember()))
			if(event.getMessage().getAttachments().size() > 0)
			{
				User user = null;
				if(args.size() > 0)
				{
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
				}
				else
					user = event.getAuthor();
				
				if(user == null)
					Actions.replyPrivate(event.getAuthor(), "Utilisateur non trouvé");
				else
				{
					if(user.getIdLong() != event.getAuthor().getIdLong() && !Utilities.isAdmin(event.getMember()))
						Actions.replyPrivate(event.getAuthor(), "Vous ne pouvez pas ajouter une image pour quelqu'un d'autre");
					else
					{
						Message.Attachment attachment = event.getMessage().getAttachments().get(0);
						String ext = attachment.getFileName().substring(attachment.getFileName().lastIndexOf("."));
						File saveFile = new File("./pictures/" + user.getIdLong() + "/", event.getMessage().getCreationTime().toEpochSecond() + ext);
						saveFile.getParentFile().mkdirs();
						if(attachment.download(saveFile) && attachment.getSize() == saveFile.length())
						{
							new PhotoConfig().addValue(user.getIdLong(), saveFile.getPath());
							Actions.giveRole(event.getGuild(), user, Roles.TROMBINOSCOPE);
							Actions.sendMessage(new PhotoChannelConfig().getTextChannel(event.getJDA()), "Oyé @here, nous avons une nouvelle photo pour %s! (ID: %d)", user.getAsMention(), event.getMessage().getCreationTime().toEpochSecond());
						}
						else
							Actions.replyPrivate(event.getAuthor(), "L'envoi a échoué");
					}
				}
			}
			else
				Actions.sendMessage(event.getAuthor().openPrivateChannel().complete(), "Veuillez joindre une image");
		return CommandResult.SUCCESS;
	}
	
	@Override
	public String getCommandUsage()
	{
		return super.getCommandUsage() + " [utilisateur]";
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
		return List.of("addphoto", "ap");
	}
}
