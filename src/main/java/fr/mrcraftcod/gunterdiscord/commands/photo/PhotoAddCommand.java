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
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.awt.*;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 13/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-13
 */
public class PhotoAddCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	PhotoAddCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NotNull final Guild guild, @NotNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("User", "The user of the picture (default: @me)", false);
	}
	
	@SuppressWarnings("Duplicates")
	@Override
	public CommandResult execute(@NotNull final MessageReceivedEvent event, @NotNull final LinkedList<String> args) throws Exception{
		Actions.deleteMessage(event.getMessage());
		if(super.execute(event, args) == CommandResult.NOT_ALLOWED){
			return CommandResult.NOT_ALLOWED;
		}
		if(event.getMessage().getAttachments().size() > 0){
			final User user;
			final var users = event.getMessage().getMentionedUsers();
			if(users.size() > 0){
				user = users.get(0);
				args.poll();
			}
			else{
				user = event.getAuthor();
			}
			
			if(user.getIdLong() != event.getAuthor().getIdLong() && !Utilities.isAdmin(event.getMember())){
				Actions.replyPrivate(event.getGuild(), event.getAuthor(), "You can't add a picture for someone else");
			}
			else{
				final var attachment = event.getMessage().getAttachments().get(0);
				final var ext = attachment.getFileName().substring(attachment.getFileName().lastIndexOf("."));
				final var saveFile = new File("./pictures/" + user.getIdLong() + "/", event.getMessage().getCreationTime().toEpochSecond() + ext);
				//noinspection ResultOfMethodCallIgnored
				saveFile.getParentFile().mkdirs();
				if(attachment.download(saveFile) && attachment.getSize() == saveFile.length()){
					new PhotoConfig(event.getGuild()).addValue(user.getIdLong(), saveFile.getPath());
					Actions.giveRole(event.getGuild(), user, new TrombinoscopeRoleConfig(event.getGuild()).getObject());
					final var builder = new EmbedBuilder();
					builder.setAuthor(user.getName(), null, user.getAvatarUrl());
					builder.setColor(Color.GREEN);
					builder.setTitle("New picture");
					builder.addField("User", user.getAsMention(), true);
					builder.addField("ID", "" + event.getMessage().getCreationTime().toEpochSecond(), true);
					Actions.sendMessage(new PhotoChannelConfig(event.getGuild()).getObject(), builder.build());
				}
				else{
					Actions.replyPrivate(event.getGuild(), event.getAuthor(), "Upload failed");
					if(saveFile.exists()){
						if(!saveFile.delete()){
							getLogger(event.getGuild()).warn("Error deleting file {}", saveFile.getAbsolutePath());
						}
					}
				}
			}
		}
		else{
			Actions.replyPrivate(event.getGuild(), event.getAuthor(), "Please add an image with the command");
		}
		return CommandResult.SUCCESS;
	}
	
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " [@user]";
	}
	
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@Override
	public String getName(){
		return "Add picture";
	}
	
	@Override
	public List<String> getCommand(){
		return List.of("add", "a");
	}
	
	@Override
	public String getDescription(){
		return "Add a picture to the trombinoscope";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
