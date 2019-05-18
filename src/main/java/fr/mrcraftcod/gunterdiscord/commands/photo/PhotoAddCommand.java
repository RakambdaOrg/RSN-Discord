package fr.mrcraftcod.gunterdiscord.commands.photo;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.settings.configs.PhotoChannelConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.PhotoConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.TrombinoscopeRoleConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.awt.Color;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
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
	
	@Override
	public CommandResult execute(final GuildMessageReceivedEvent event, @NotNull final LinkedList<String> args) throws Exception{
		Actions.deleteMessage(event.getMessage());
		super.execute(event, args);
		if(!event.getMessage().getAttachments().isEmpty()){
			final User user;
			final var users = event.getMessage().getMentionedUsers();
			if(!users.isEmpty()){
				user = users.get(0);
				args.poll();
			}
			else{
				user = event.getAuthor();
			}
			
			if(!Objects.equals(user, event.getAuthor()) && !Utilities.isAdmin(event.getMember())){
				Actions.replyPrivate(event.getGuild(), event.getAuthor(), "You can't add a picture for someone else");
			}
			else{
				for(final var attachment : event.getMessage().getAttachments()){
					final var ext = attachment.getFileName().substring(attachment.getFileName().lastIndexOf("."));
					final var savePath = Paths.get("./pictures").resolve("" + user.getIdLong()).resolve(event.getMessage().getTimeCreated().toEpochSecond() + ext);
					//noinspection ResultOfMethodCallIgnored
					savePath.getParent().toFile().mkdirs();
					final var future = attachment.downloadToFile(savePath.toFile());
					final var saveFile = future.get();
					if(!future.isCompletedExceptionally() && Objects.equals(attachment.getSize(), saveFile.length()) && saveFile.length() > 512){
						new PhotoConfig(event.getGuild()).addValue(user.getIdLong(), saveFile.getPath());
						Actions.giveRole(event.getGuild(), user, new TrombinoscopeRoleConfig(event.getGuild()).getObject());
						final var builder = new EmbedBuilder();
						builder.setAuthor(user.getName(), null, user.getAvatarUrl());
						builder.setColor(Color.GREEN);
						builder.setTitle("New picture");
						builder.addField("User", user.getAsMention(), true);
						builder.addField("ID", "" + event.getMessage().getTimeCreated().toEpochSecond(), true);
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
