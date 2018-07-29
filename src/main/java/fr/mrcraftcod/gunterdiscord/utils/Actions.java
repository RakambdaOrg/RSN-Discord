package fr.mrcraftcod.gunterdiscord.utils;

import fr.mrcraftcod.gunterdiscord.Main;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.GenericMessageEvent;
import net.dv8tion.jda.core.exceptions.HierarchyException;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import static fr.mrcraftcod.gunterdiscord.utils.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-04-15
 */
@SuppressWarnings({
		"WeakerAccess",
		"unused"
})
public class Actions{
	public static final Consumer<Message> PIN_MESSAGE = m -> m.pin().queue();
	
	/**
	 * Reply to a message.
	 *
	 * @param event  The message event.
	 * @param format The message format.
	 * @param args   The message parameters.
	 */
	public static void reply(@NotNull GenericMessageEvent event, @NotNull String format, Object... args){
		reply(event, String.format(format, args));
	}
	
	/**
	 * Reply to a message.
	 *
	 * @param event The message event.
	 * @param text  The text to send.
	 */
	@SuppressWarnings("Duplicates")
	public static void reply(@NotNull GenericMessageEvent event, String text){
		switch(event.getChannelType()){
			case PRIVATE:
				sendMessage(event.getGuild(), event.getPrivateChannel(), text);
				break;
			case TEXT:
				sendMessage(event.getTextChannel(), text);
				break;
		}
	}
	
	/**
	 * Send a message to a channel.
	 *
	 * @param guild   The guild that initiated the event.
	 * @param channel The channel to send to.
	 * @param text    The message to send.
	 */
	public static void sendMessage(Guild guild, @NotNull PrivateChannel channel, String text){
		if(channel.getUser().isBot()){
			getLogger(guild).info("Cannot send private message to bot {} : {}", Utilities.getUserToLog(channel.getUser()), text);
		}
		else{
			channel.sendMessage(text).queue();
			getLogger(guild).info("Sent private message to {} : {}", Utilities.getUserToLog(channel.getUser()), text);
		}
	}
	
	/**
	 * Send a message to a channel.
	 *
	 * @param channel The channel to send to.
	 * @param text    The message to send.
	 */
	public static void sendMessage(@NotNull TextChannel channel, String text){
		sendMessage(channel, null, text);
	}
	
	/**
	 * Send a message to a channel.
	 *
	 * @param channel The channel to send to.
	 * @param onDone  The action to do when done.
	 * @param text    The message to send.
	 */
	@SuppressWarnings("Duplicates")
	public static void sendMessage(@NotNull TextChannel channel, Consumer<Message> onDone, String text){
		if(channel.canTalk()){
			if(onDone != null){
				channel.sendMessage(text).queue(onDone);
			}
			else{
				channel.sendMessage(text).queue();
			}
			getLogger(channel.getGuild()).info("Sent message to {} : {}", channel.getName(), text);
		}
		else{
			getLogger(channel.getGuild()).error("Access denied to text channel: {}", channel.getAsMention());
		}
	}
	
	/**
	 * Send a message to a channel.
	 *
	 * @param channel The channel to send to.
	 * @param format  The format fo the message.
	 * @param args    The message parameters.
	 */
	public static void sendMessage(@NotNull TextChannel channel, @NotNull String format, Object... args){
		sendMessage(channel, String.format(format, args));
	}
	
	/**
	 * Send a message to a channel.
	 *
	 * @param channel The channel to send to.
	 * @param onDone  The action to do when done.
	 * @param format  The format fo the message.
	 * @param args    The message parameters.
	 */
	public static void sendMessage(@NotNull TextChannel channel, Consumer<Message> onDone, @NotNull String format, Object... args){
		sendMessage(channel, onDone, String.format(format, args));
	}
	
	/**
	 * Send a message to a user.
	 *
	 * @param guild  The guild that initiated the event.
	 * @param user   The user to send to.
	 * @param format The format fo the message.
	 * @param args   The message parameters.
	 */
	public static void replyPrivate(Guild guild, @NotNull User user, String format, Object... args){
		replyPrivate(guild, user, String.format(format, args));
	}
	
	/**
	 * Send a message to a user.
	 *
	 * @param guild The guild that initiated the event.
	 * @param user  The user to send to.
	 * @param text  The message to send.
	 */
	public static void replyPrivate(Guild guild, @NotNull User user, String text){
		user.openPrivateChannel().queue(channel -> sendMessage(guild, channel, text));
	}
	
	/**
	 * Remove a role from a user.
	 *
	 * @param user The user to remove the role from.
	 * @param role The role to remove.
	 */
	public static void removeRole(User user, @NotNull Role role){
		Optional.ofNullable(role.getGuild().getMember(user)).ifPresentOrElse(member -> removeRole(member, role), () -> getLogger(role.getGuild()).info("Couldn't find {} in guild {}", Utilities.getUserToLog(user), Utilities.getGuildToLog(role.getGuild())));
	}
	
	/**
	 * Remove a role from a user.
	 *
	 * @param member The user to remove the role from.
	 * @param role   The role to remove.
	 */
	public static void removeRole(@NotNull Member member, Role role){
		try{
			//noinspection ConstantConditions
			member.getGuild().getController().removeSingleRoleFromMember(member, role).queue();
			getLogger(member.getGuild()).info("Removed role {} from {}", role, Utilities.getUserToLog(member.getUser()));
		}
		catch(IllegalArgumentException e){
			getLogger(member.getGuild()).warn("User/Role not found", e);
		}
	}
	
	/**
	 * Remove roles from a user.
	 *
	 * @param member The user to remove the roles from.
	 * @param roles  The roles to remove.
	 */
	public static void removeRole(Member member, @NotNull List<Role> roles){
		roles.forEach(role -> removeRole(member, role));
	}
	
	/**
	 * Delete a message.
	 *
	 * @param message The message to delete.
	 */
	public static void deleteMessage(@NotNull Message message){
		message.delete().queue();
		getLogger(message.getGuild()).info("Deleted message from {} : {}", Utilities.getUserToLog(message.getAuthor()), message.getContentRaw());
	}
	
	private static String getMessageForLog(Message message){
		return message.getContentRaw() + " => " + message.getEmbeds().stream().map(Utilities::getEmbedForLog).collect(Collectors.joining(" | "));
	}
	
	/**
	 * Send a file to a channel.
	 *
	 * @param channel  The channel to send to.
	 * @param resource The resource to send.
	 * @param name     The name of the file.
	 */
	public static void sendFile(@NotNull TextChannel channel, String resource, String name){
		sendFile(channel, Main.class.getResourceAsStream(resource), name);
	}
	
	/**
	 * Send a message and gets it.
	 *
	 * @param channel the channel to send to.
	 * @param text    The message to send.
	 *
	 * @return The message sent or null fi there was a problem.
	 */
	public static Message getMessage(@NotNull TextChannel channel, String text){
		getLogger(channel.getGuild()).info("Sent message to {} : {}", channel.getName(), text);
		return channel.sendMessage(text).complete();
	}
	
	/**
	 * Send a message and gets it.
	 *
	 * @param channel the channel to send to.
	 * @param format  The message format.
	 * @param args    The parameters of the message.
	 *
	 * @return The message sent or null fi there was a problem.
	 */
	public static Message getMessage(@NotNull TextChannel channel, @NotNull String format, Object... args){
		return getMessage(channel, String.format(format, args));
	}
	
	/**
	 * Send a file to a channel.
	 *
	 * @param channel The channel to send to.
	 * @param file    The file to send.
	 */
	public static void sendFile(@NotNull TextChannel channel, @NotNull File file){
		try{
			sendFile(channel, new FileInputStream(file), file.getName());
		}
		catch(FileNotFoundException e){
			getLogger(channel.getGuild()).error("Error sending file {}", file.getAbsolutePath(), e);
		}
	}
	
	/**
	 * Send a file to a channel.
	 *
	 * @param channel The channel to send to.
	 * @param stream  The data to send.
	 * @param name    The name of the file.
	 */
	public static void sendFile(@NotNull TextChannel channel, InputStream stream, String name){
		channel.sendFile(stream, name).queue();
		getLogger(channel.getGuild()).info("Sent file {} to {}", name, channel.getName());
	}
	
	/**
	 * Give roles to the user.
	 *
	 * @param member The member.
	 * @param roles  The roles IDs.
	 */
	public static void giveRole(@NotNull Member member, @NotNull List<Long> roles){
		giveRole(member.getUser(), roles.stream().map(r -> getRoleByID(member.getGuild(), r)).collect(Collectors.toList()));
	}
	
	/**
	 * Give roles to a user.
	 *
	 * @param user  The user to set the role to.
	 * @param roles The roles to set.
	 */
	public static void giveRole(@NotNull User user, @NotNull List<Role> roles){
		roles.forEach(role -> giveRole(role.getGuild(), user, role));
	}
	
	/**
	 * Get a role by its ID.
	 *
	 * @param guild The guild the role is in.
	 * @param role  The ID of the role.
	 *
	 * @return The role or null if not found.
	 */
	public static Role getRoleByID(@NotNull Guild guild, Long role){
		return guild.getRoleById(role);
	}
	
	/**
	 * Give a role to a user.
	 *
	 * @param guild The guild the role is in.
	 * @param user  The user to set the role to.
	 * @param role  The role to set.
	 */
	public static void giveRole(@NotNull Guild guild, @NotNull User user, Role role){
		try{
			Member member = guild.getMember(user);
			if(member.getRoles().contains(role)){
				getLogger(guild).info("{} already have role {}", Utilities.getUserToLog(user), role);
			}
			else{
				//noinspection ConstantConditions
				guild.getController().addSingleRoleToMember(guild.getMember(user), role).queue();
				getLogger(guild).info("Added role {} to {}", role, Utilities.getUserToLog(user));
			}
		}
		catch(IllegalArgumentException e){
			getLogger(guild).warn("User/Role not found {}", role, e);
		}
		catch(Exception e){
			getLogger(guild).error("Error giving role {} to {}", role, Utilities.getUserToLog(user), e);
		}
	}
	
	/**
	 * Reply to an event?
	 *
	 * @param event The event.
	 * @param embed The message to send.
	 */
	@SuppressWarnings("Duplicates")
	public static void reply(@NotNull GenericMessageEvent event, MessageEmbed embed){
		switch(event.getChannelType()){
			case PRIVATE:
				sendPrivateMessage(event.getGuild(), event.getPrivateChannel(), embed);
				break;
			case TEXT:
				sendMessage(event.getTextChannel(), embed);
				break;
		}
	}
	
	/**
	 * Send a message to a channel.
	 *
	 * @param guild   The guild the event is from.
	 * @param channel The channel to send to.
	 * @param embed   The message to send.
	 */
	public static void sendPrivateMessage(Guild guild, PrivateChannel channel, MessageEmbed embed){
		if(channel != null){
			channel.sendMessage(embed).queue();
			getLogger(guild).info("Sent private message to {} : {}", Utilities.getUserToLog(channel.getUser()), Utilities.getEmbedForLog(embed));
		}
		else{
			getLogger(guild).warn("Cannot send private message to null channel : {}", Utilities.getEmbedForLog(embed));
		}
	}
	
	/**
	 * Send a message to a channel.
	 *
	 * @param channel The channel to send to.
	 * @param embed   The message to send.
	 */
	public static void sendMessage(@NotNull TextChannel channel, MessageEmbed embed){
		sendMessage(channel, null, embed);
	}
	
	/**
	 * Send a message to a channel.
	 *
	 * @param channel The channel to send to.
	 * @param onDone  The action to do when done.
	 * @param embed   The message to send.
	 */
	@SuppressWarnings("Duplicates")
	public static void sendMessage(@NotNull TextChannel channel, Consumer<Message> onDone, MessageEmbed embed){
		if(channel.canTalk()){
			if(onDone != null){
				channel.sendMessage(embed).queue(onDone);
			}
			else{
				channel.sendMessage(embed).queue();
			}
			getLogger(channel.getGuild()).info("Sent message to {} : {}", channel.getName(), Utilities.getEmbedForLog(embed));
		}
		else{
			getLogger(channel.getGuild()).error("Access denied to text channel: {}, when sending: {}", channel.getAsMention(), Utilities.getEmbedForLog(embed));
		}
	}
	
	/**
	 * Send a message to a channel.
	 *
	 * @param channel The channel to send to.
	 * @param embeds  The messages to send.
	 */
	public static void sendMessage(@NotNull TextChannel channel, @NotNull List<MessageEmbed> embeds){
		embeds.forEach(e -> sendMessage(channel, e));
	}
	
	/**
	 * Deafen members.
	 *
	 * @param members The member to set deaf.
	 * @param state   True if deaf, false is not deaf.
	 */
	public static void deafen(@NotNull List<Member> members, boolean state){
		members.forEach(m -> deafen(m, state));
	}
	
	/**
	 * Deafen a member.
	 *
	 * @param member The member to set deaf.
	 * @param state  True if deaf, false is not deaf.
	 */
	private static void deafen(@NotNull Member member, boolean state){
		try{
			member.getGuild().getController().setDeafen(member, state).queue();
			getLogger(member.getGuild()).info("Member {} is now {}deaf", Utilities.getUserToLog(member.getUser()), state ? "" : "un");
		}
		catch(HierarchyException | InsufficientPermissionException e){
			getLogger(member.getGuild()).warn("Cannot {}deaf member {}", state ? "" : "un", Utilities.getUserToLog(member.getUser()));
		}
		catch(Exception e){
			getLogger(member.getGuild()).error("Error trying to {}deafen member {}", state ? "" : "un", Utilities.getUserToLog(member.getUser()), e);
		}
	}
	
	/**
	 * Deny a person for members.
	 *
	 * @param members    The members concerned.
	 * @param channel    The channel it'll apply to.
	 * @param permission The permission to remove.
	 */
	public static void denyPermission(@NotNull List<Member> members, @NotNull Channel channel, @NotNull Permission permission){
		members.forEach(m -> denyPermission(m, channel, permission));
	}
	
	/**
	 * Deny a person for a member.
	 *
	 * @param member     The member concerned.
	 * @param channel    The channel it'll apply to.
	 * @param permission The permission to remove.
	 */
	public static void denyPermission(@NotNull Member member, @NotNull Channel channel, @NotNull Permission permission){
		try{
			channel.putPermissionOverride(member).setDeny(permission).queue();
			getLogger(member.getGuild()).info("{} no longer have permission {} on {}", Utilities.getUserToLog(member.getUser()), permission.name(), channel.getName());
		}
		catch(HierarchyException | InsufficientPermissionException e){
			getLogger(member.getGuild()).warn("Cannot remove permission from {} in {}", Utilities.getUserToLog(member.getUser()), channel.getName());
		}
		catch(Exception e){
			getLogger(member.getGuild()).warn("Error removing permission from {} in {}", Utilities.getUserToLog(member.getUser()), channel.getName(), e);
		}
	}
	
	/**
	 * Allow a person for members.
	 *
	 * @param members    The members concerned.
	 * @param channel    The channel it'll apply to.
	 * @param permission The permission to give.
	 */
	public static void allowPermission(@NotNull List<Member> members, @NotNull Channel channel, @NotNull Permission permission){
		members.forEach(m -> allowPermission(m, channel, permission));
	}
	
	/**
	 * Allow a person for a member.
	 *
	 * @param member     The member concerned.
	 * @param channel    The channel it'll apply to.
	 * @param permission The permission to give.
	 */
	public static void allowPermission(@NotNull Member member, @NotNull Channel channel, @NotNull Permission permission){
		try{
			channel.putPermissionOverride(member).setAllow(permission).queue();
			getLogger(member.getGuild()).info("{} now have permission {} on {}", Utilities.getUserToLog(member.getUser()), permission.getName(), channel.getName());
		}
		catch(HierarchyException | InsufficientPermissionException e){
			getLogger(member.getGuild()).warn("Cannot give permission to {} in {}", Utilities.getUserToLog(member.getUser()), channel.getName());
		}
		catch(Exception e){
			getLogger(member.getGuild()).warn("Error giving permission to {} in {}", Utilities.getUserToLog(member.getUser()), channel.getName(), e);
		}
	}
	
	public static List<Message> getMessage(TextChannel channel, List<MessageEmbed> embeds){
		return embeds.stream().map(embed -> getMessage(channel, embed)).collect(Collectors.toList());
	}
	
	/**
	 * Send a message and gets it.
	 *
	 * @param channel the channel to send to.
	 * @param embed   The message to send.
	 *
	 * @return The message sent or null fi there was a problem.
	 */
	public static Message getMessage(@NotNull TextChannel channel, MessageEmbed embed){
		getLogger(channel.getGuild()).info("Sent message to {} : {}", channel.getName(), embed);
		return channel.sendMessage(embed).complete();
	}
}
