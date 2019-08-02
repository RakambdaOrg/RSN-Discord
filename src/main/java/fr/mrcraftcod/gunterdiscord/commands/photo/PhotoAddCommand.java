package fr.mrcraftcod.gunterdiscord.commands.photo;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.settings.NewSettings;
import fr.mrcraftcod.gunterdiscord.settings.guild.trombinoscope.PhotoEntryConfiguration;
import fr.mrcraftcod.gunterdiscord.settings.types.ChannelConfiguration;
import fr.mrcraftcod.gunterdiscord.settings.types.RoleConfiguration;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
	private static final int MIN_SIZE = 512;
	
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	PhotoAddCommand(@Nullable final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@Nonnull final Guild guild, @Nonnull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("User", "The user of the picture (default: @me)", false);
	}
	
	@Nonnull
	@Override
	public CommandResult execute(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args) throws Exception{
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
					final var savePath = Paths.get("./pictures").resolve(String.valueOf(user.getIdLong())).resolve(event.getMessage().getTimeCreated().toEpochSecond() + ext);
					//noinspection ResultOfMethodCallIgnored
					savePath.getParent().toFile().mkdirs();
					final var future = attachment.downloadToFile(savePath.toFile());
					final var saveFile = future.get();
					if(!future.isCompletedExceptionally() && Objects.equals(attachment.getSize(), saveFile.length()) && saveFile.length() > MIN_SIZE){
						NewSettings.getConfiguration(event.getGuild()).getTrombinoscopeConfiguration().getPhotos(user).add(new PhotoEntryConfiguration(user, saveFile.getPath()));
						NewSettings.getConfiguration(event.getGuild()).getTrombinoscopeConfiguration().getParticipantRole().flatMap(RoleConfiguration::getRole).ifPresent(r -> Actions.giveRole(event.getGuild(), user, r));
						final var builder = new EmbedBuilder();
						builder.setAuthor(user.getName(), null, user.getAvatarUrl());
						builder.setColor(Color.GREEN);
						builder.setTitle("New picture");
						builder.addField("User", user.getAsMention(), true);
						builder.addField("ID", String.valueOf(event.getMessage().getTimeCreated().toEpochSecond()), true);
						Actions.sendMessage(NewSettings.getConfiguration(event.getGuild()).getTrombinoscopeConfiguration().getPhotoChannel().flatMap(ChannelConfiguration::getChannel).orElse(event.getChannel()), builder.build());
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
		Actions.deleteMessage(event.getMessage());
		return CommandResult.SUCCESS;
	}
	
	@Nonnull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " [@user]";
	}
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Add picture";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("add", "a");
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return "Add a picture to the trombinoscope";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
