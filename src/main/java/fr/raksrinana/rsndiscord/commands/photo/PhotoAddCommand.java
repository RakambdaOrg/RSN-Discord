package fr.raksrinana.rsndiscord.commands.photo;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.commands.generic.NotAllowedException;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.trombinoscope.PhotoEntryConfiguration;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.log.Log;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.awt.Color;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class PhotoAddCommand extends BasicCommand{
	private static final int MIN_SIZE = 512;
	
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	PhotoAddCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("User", "The user of the picture (default: @me)", false);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
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
				throw new NotAllowedException("You can't add a picture for someone else");
			}
			else{
				for(final var attachment : event.getMessage().getAttachments()){
					final var ext = attachment.getFileName().substring(attachment.getFileName().lastIndexOf("."));
					final var savePath = Paths.get("./pictures").resolve(String.valueOf(user.getIdLong())).resolve(event.getMessage().getTimeCreated().toEpochSecond() + ext);
					savePath.getParent().toFile().mkdirs();
					attachment.downloadToFile(savePath.toFile()).thenAccept(saveFile -> {
						if(Objects.equals(attachment.getSize(), saveFile.length()) && saveFile.length() > MIN_SIZE){
							Settings.get(event.getGuild()).getTrombinoscopeConfiguration().getPhotos(user).add(new PhotoEntryConfiguration(user, saveFile.getPath()));
							Settings.get(event.getGuild()).getTrombinoscopeConfiguration().getParticipantRole().flatMap(RoleConfiguration::getRole).ifPresent(r -> Optional.ofNullable(r.getGuild().getMember(user)).ifPresent(member -> Actions.giveRole(member, r)));
							final var builder = new EmbedBuilder();
							builder.setAuthor(user.getName(), null, user.getAvatarUrl());
							builder.setColor(Color.GREEN);
							builder.setTitle("New picture");
							builder.addField("User", user.getAsMention(), true);
							builder.addField("ID", String.valueOf(event.getMessage().getTimeCreated().toEpochSecond()), true);
							Actions.sendMessage(Settings.get(event.getGuild()).getTrombinoscopeConfiguration().getPhotoChannel().flatMap(ChannelConfiguration::getChannel).orElse(event.getChannel()), "", builder.build());
						}
						else{
							Actions.replyPrivate(event.getGuild(), event.getAuthor(), MessageFormat.format("Upload failed, attachment size is {}, file size is {}", attachment.getSize(), saveFile.length()), null);
							if(savePath.toFile().exists()){
								if(!savePath.toFile().delete()){
									Log.getLogger(event.getGuild()).warn("Error deleting file {}", savePath);
								}
							}
						}
					}).exceptionally(throwable -> {
						Log.getLogger(event.getGuild()).error("Failed to download file", throwable);
						Actions.replyPrivate(event.getGuild(), event.getAuthor(), "Upload failed", null);
						if(savePath.toFile().exists()){
							if(!savePath.toFile().delete()){
								Log.getLogger(event.getGuild()).warn("Error deleting file {}", savePath);
							}
						}
						return null;
					});
				}
			}
		}
		else{
			Actions.replyPrivate(event.getGuild(), event.getAuthor(), "Please add an image with the command", null);
		}
		Actions.deleteMessage(event.getMessage());
		return CommandResult.SUCCESS;
	}
	
	@NonNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " [@user]";
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Add picture";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("add", "a");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Add a picture to the trombinoscope";
	}
}
