package fr.raksrinana.rsndiscord.utils;

import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.utils.log.Log;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GenericGuildMessageEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
	public static final Consumer<Message> PIN_MESSAGE = message -> message.pin().queue();
	
	/**
	 * Reply to a message.
	 *
	 * @param event  The message event.
	 * @param format The message format.
	 * @param args   The message parameters.
	 */
	public static void replyFormatted(final GenericGuildMessageEvent event, final String format, final Object... args){
		reply(event, String.format(format, args));
	}
	
	/**
	 * Reply to a message.
	 *
	 * @param event   The message event.
	 * @param message The message.
	 */
	public static void reply(final GenericGuildMessageEvent event, final String message){
		sendMessage(event.getChannel(), message);
	}
	
	/**
	 * Send a message to a channel.
	 *
	 * @param channel The channel to send to.
	 * @param text    The message to send.
	 */
	public static void sendMessage(final TextChannel channel, final String text){
		sendMessage(channel, null, text);
	}
	
	/**
	 * Send a message to a channel.
	 *
	 * @param channel The channel to send to.
	 * @param onDone  The action to do when done.
	 * @param text    The message to send.
	 */
	public static void sendMessage(final TextChannel channel, final Consumer<Message> onDone, final String text){
		if(channel.canTalk()){
			if(Objects.nonNull(onDone)){
				channel.sendMessage(text).queue(onDone);
			}
			else{
				channel.sendMessage(text).queue();
			}
			Log.getLogger(channel.getGuild()).info("Sent message to {} : {}", channel.getName(), text);
		}
		else{
			Log.getLogger(channel.getGuild()).error("Access denied to text channel: {}", channel.getAsMention());
		}
	}
	
	/**
	 * Reply to a message.
	 *
	 * @param event  The message event.
	 * @param onDone The action to do when done.
	 * @param format The message format.
	 * @param args   The message parameters.
	 */
	public static void reply(final GenericGuildMessageEvent event, final Consumer<Message> onDone, final String format, final Object... args){
		sendMessage(event.getChannel(), onDone, String.format(format, args));
	}
	
	/**
	 * Reply to a message.
	 *
	 * @param event   The message event.
	 * @param onDone  The action to do when done.
	 * @param message The embed to send.
	 */
	public static void reply(final GenericGuildMessageEvent event, final Consumer<Message> onDone, final MessageEmbed message){
		sendMessage(event.getChannel(), onDone, message);
	}
	
	/**
	 * Send a message to a channel.
	 *
	 * @param channel The channel to send to.
	 * @param onDone  The action to do when done.
	 * @param embed   The message to send.
	 */
	public static void sendMessage(final TextChannel channel, final Consumer<Message> onDone, final MessageEmbed embed){
		if(channel.canTalk()){
			if(Objects.nonNull(onDone)){
				channel.sendMessage(embed).queue(onDone);
			}
			else{
				channel.sendMessage(embed).queue();
			}
			Log.getLogger(channel.getGuild()).info("Sent message to {} : {}", channel.getName(), Utilities.getEmbedForLog(embed));
		}
		else{
			Log.getLogger(channel.getGuild()).error("Access denied to text channel: {}, when sending: {}", channel.getAsMention(), Utilities.getEmbedForLog(embed));
		}
	}
	
	/**
	 * Send a message to a channel with an embed.
	 *
	 * @param channel The channel to send to.
	 * @param text    The message to send.
	 * @param embed   The embed to attach.
	 */
	public static void sendMessage(final TextChannel channel, final String text, final MessageEmbed embed){
		sendMessage(channel, text, embed, null);
	}
	
	/**
	 * Send a message to a channel with an embed.
	 *
	 * @param channel The channel to send to.
	 * @param text    The message to send.
	 * @param embed   The embed to attach.
	 * @param onSend  A callback to execute when the message is sent.
	 */
	public static void sendMessage(final TextChannel channel, final String text, final MessageEmbed embed, Consumer<Message> onSend){
		if(channel.canTalk()){
			channel.sendMessage(text).embed(embed).queue(onSend);
			Log.getLogger(channel.getGuild()).info("Sent message to {} : {} {}", channel.getName(), text, Utilities.getEmbedForLog(embed));
		}
		else{
			Log.getLogger(channel.getGuild()).error("Access denied to text channel: {}", channel.getAsMention());
		}
	}
	
	/**
	 * Send a message to a channel.
	 *
	 * @param channel The channel to send to.
	 * @param format  The format fo the message.
	 * @param args    The message parameters.
	 */
	public static void sendMessage(final TextChannel channel, final String format, final Object... args){
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
	public static void sendMessage(final TextChannel channel, final Consumer<Message> onDone, final String format, final Object... args){
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
	public static void replyPrivate(final Guild guild, final User user, final String format, final Object... args){
		replyPrivate(guild, user, String.format(format, args));
	}
	
	/**
	 * Send a message to a user.
	 *
	 * @param guild The guild that initiated the event.
	 * @param user  The user to send to.
	 * @param text  The message to send.
	 */
	public static void replyPrivate(final Guild guild, final User user, final String text){
		user.openPrivateChannel().queue(channel -> sendMessage(guild, channel, text));
	}
	
	/**
	 * Send a message to a channel.
	 *
	 * @param guild   The guild that initiated the event.
	 * @param channel The channel to send to.
	 * @param text    The message to send.
	 */
	public static void sendMessage(final Guild guild, final PrivateChannel channel, final String text){
		if(channel.getUser().isBot()){
			Log.getLogger(guild).info("Cannot send private message to bot {} : {}", channel.getUser(), text);
		}
		else{
			channel.sendMessage(text).queue();
			Log.getLogger(guild).info("Sent private message to {} : {}", channel.getUser(), text);
		}
	}
	
	/**
	 * Remove a role from a user.
	 *
	 * @param user The user to remove the role from.
	 * @param role The role to remove.
	 */
	public static void removeRole(final User user, final Role role){
		Optional.ofNullable(role.getGuild().getMember(user)).ifPresentOrElse(member -> removeRole(member, role), () -> Log.getLogger(role.getGuild()).info("Couldn't find {} in guild {}", user, role.getGuild()));
	}
	
	/**
	 * Remove a role from a user.
	 *
	 * @param member The user to remove the role from.
	 * @param role   The role to remove.
	 */
	public static void removeRole(final Member member, final Role role){
		if(Objects.nonNull(member)){
			try{
				member.getGuild().removeRoleFromMember(member, role).queue();
				Log.getLogger(member.getGuild()).info("Removed role {} from {}", role, member.getUser());
			}
			catch(final IllegalArgumentException e){
				Log.getLogger(member.getGuild()).warn("User/Role not found", e);
			}
		}
	}
	
	/**
	 * Delete a message.
	 *
	 * @param message The message to delete.
	 */
	public static void deleteMessage(final Message message){
		if(Objects.nonNull(message)){
			message.delete().queue();
			Log.getLogger(message.getGuild()).info("Deleted message from {} : {}", message.getAuthor(), message.getContentRaw());
		}
	}
	
	/**
	 * Gets the representation of a message for logs.
	 *
	 * @param message The message to log.
	 *
	 * @return Its representation.
	 */
	public static String getMessageForLog(final Message message){
		return message.getContentRaw() + " => " + message.getEmbeds().stream().map(Utilities::getEmbedForLog).collect(Collectors.joining(" | "));
	}
	
	/**
	 * Remove roles from a user.
	 *
	 * @param member The user to remove the roles from.
	 * @param roles  The roles to remove.
	 */
	public static void removeRole(final Member member, final List<Role> roles){
		roles.forEach(role -> removeRole(member, role));
	}
	
	/**
	 * Send a file to a channel.
	 *
	 * @param channel  The channel to send to.
	 * @param resource The resource to send.
	 * @param name     The name of the file.
	 */
	public static void sendFile(final TextChannel channel, final String resource, final String name){
		sendFile(channel, Main.class.getResourceAsStream(resource), name);
	}
	
	/**
	 * Send a file to a channel.
	 *
	 * @param channel The channel to send to.
	 * @param stream  The data to send.
	 * @param name    The name of the file.
	 */
	public static void sendFile(final TextChannel channel, final InputStream stream, final String name){
		channel.sendFile(stream, name).queue();
		Log.getLogger(channel.getGuild()).info("Sent file {} to {}", name, channel.getName());
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
	public static Message getMessage(final TextChannel channel, final String format, final Object... args){
		return getMessage(channel, String.format(format, args));
	}
	
	/**
	 * Send a message and gets it.
	 *
	 * @param channel the channel to send to.
	 * @param text    The message to send.
	 *
	 * @return The message sent or null fi there was a problem.
	 */
	public static Message getMessage(final TextChannel channel, final String text){
		Log.getLogger(channel.getGuild()).info("Sent message to {} : {}", channel.getName(), text);
		return channel.sendMessage(text).complete();
	}
	
	/**
	 * Send a file to a channel.
	 *
	 * @param channel The channel to send to.
	 * @param file    The file to send.
	 */
	public static void sendFile(final TextChannel channel, final File file){
		try{
			sendFile(channel, new FileInputStream(file), file.getName());
		}
		catch(final FileNotFoundException e){
			Log.getLogger(channel.getGuild()).error("Error sending file {}", file.getAbsolutePath(), e);
		}
	}
	
	/**
	 * Give roles to the user.
	 *
	 * @param member The member.
	 * @param roles  The roles IDs.
	 */
	public static void giveRole(final Member member, final List<Long> roles){
		giveRole(member.getUser(), roles.stream().map(r -> getRoleByID(member.getGuild(), r)).collect(Collectors.toList()));
	}
	
	/**
	 * Give roles to a user.
	 *
	 * @param user  The user to set the role to.
	 * @param roles The roles to set.
	 */
	public static void giveRole(final User user, final List<Role> roles){
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
	public static Role getRoleByID(final Guild guild, final Long role){
		return guild.getRoleById(role);
	}
	
	/**
	 * Give a role to a user.
	 *
	 * @param guild The guild the role is in.
	 * @param user  The user to set the role to.
	 * @param role  The role to set.
	 */
	public static void giveRole(final Guild guild, final User user, final Role role){
		try{
			final var member = guild.getMember(user);
			if(Objects.requireNonNull(member).getRoles().contains(role)){
				Log.getLogger(guild).info("{} already have role {}", user, role);
			}
			else{
				guild.addRoleToMember(member, role).queue();
				Log.getLogger(guild).info("Added role {} to {}", role, user);
			}
		}
		catch(final IllegalArgumentException e){
			Log.getLogger(guild).warn("User/Role not found {}", role, e);
		}
		catch(final Exception e){
			Log.getLogger(guild).error("Error giving role {} to {}", role, user, e);
		}
	}
	
	/**
	 * Send a message to a channel.
	 *
	 * @param guild   The guild the event is from.
	 * @param channel The channel to send to.
	 * @param embed   The message to send.
	 */
	public static void sendPrivateMessage(final Guild guild, final PrivateChannel channel, final MessageEmbed embed){
		if(Objects.nonNull(channel)){
			channel.sendMessage(embed).queue();
			Log.getLogger(guild).info("Sent private message to {} : {}", channel.getUser(), Utilities.getEmbedForLog(embed));
		}
		else{
			Log.getLogger(guild).warn("Cannot send private message to null channel : {}", Utilities.getEmbedForLog(embed));
		}
	}
	
	/**
	 * Send a message to a channel.
	 *
	 * @param guild   The guild the event is from.
	 * @param channel The channel to send to.
	 * @param message The message's text.
	 * @param embed   The message to send.
	 */
	public static void sendPrivateMessage(final Guild guild, final PrivateChannel channel, final String message, final MessageEmbed embed){
		if(Objects.nonNull(channel)){
			channel.sendMessage(message).embed(embed).queue();
			Log.getLogger(guild).info("Sent private message to {} : {} {}", channel.getUser(), message, Utilities.getEmbedForLog(embed));
		}
		else{
			Log.getLogger(guild).warn("Cannot send private message to null channel : {} {}", message, Utilities.getEmbedForLog(embed));
		}
	}
	
	/**
	 * Reply to an event?
	 *
	 * @param event The event.
	 * @param embed The message to send.
	 */
	public static void reply(final GenericGuildMessageEvent event, final MessageEmbed embed){
		sendMessage(event.getChannel(), embed);
	}
	
	/**
	 * Send a message to a channel.
	 *
	 * @param channel The channel to send to.
	 * @param embed   The message to send.
	 */
	public static void sendMessage(final TextChannel channel, final MessageEmbed embed){
		sendMessage(channel, (Consumer<Message>) null, embed);
	}
	
	/**
	 * Send a message to a channel.
	 *
	 * @param channel The channel to send to.
	 * @param embeds  The messages to send.
	 */
	public static void sendMessage(final TextChannel channel, final List<MessageEmbed> embeds){
		embeds.forEach(e -> sendMessage(channel, e));
	}
	
	/**
	 * Send a message to a channel.
	 *
	 * @param channel The channel to send to.
	 * @param embeds  The messages to send.
	 */
	public static void sendMessage(final TextChannel channel, final Consumer<Message> onDone, final List<MessageEmbed> embeds){
		embeds.forEach(e -> sendMessage(channel, onDone, e));
	}
	
	/**
	 * Deafen members.
	 *
	 * @param members The member to set deaf.
	 * @param state   True if deaf, false is not deaf.
	 */
	public static void deafen(final List<Member> members, final boolean state){
		members.forEach(member -> deafen(member, state));
	}
	
	/**
	 * Deafen a member.
	 *
	 * @param member The member to set deaf.
	 * @param state  True if deaf, false is not deaf.
	 */
	private static void deafen(final Member member, final boolean state){
		try{
			member.getGuild().deafen(member, state).queue();
			Log.getLogger(member.getGuild()).info("Member {} is now {}deaf", member.getUser(), state ? "" : "un");
		}
		catch(final HierarchyException | InsufficientPermissionException e){
			Log.getLogger(member.getGuild()).warn("Cannot {}deaf member {}", state ? "" : "un", member.getUser());
		}
		catch(final Exception e){
			Log.getLogger(member.getGuild()).error("Error trying to {}deafen member {}", state ? "" : "un", member.getUser(), e);
		}
	}
	
	/**
	 * Deny a person for members.
	 *
	 * @param members    The members concerned.
	 * @param channel    The channel it'll apply to.
	 * @param permission The permission to remove.
	 */
	public static void denyPermission(final List<Member> members, final GuildChannel channel, final Permission permission){
		members.forEach(member -> denyPermission(member, channel, permission));
	}
	
	/**
	 * Deny a person for a member.
	 *
	 * @param member     The member concerned.
	 * @param channel    The channel it'll apply to.
	 * @param permission The permission to remove.
	 */
	public static void denyPermission(final Member member, final GuildChannel channel, final Permission permission){
		try{
			channel.putPermissionOverride(member).setDeny(permission).queue();
			Log.getLogger(member.getGuild()).info("{} no longer have permission {} on {}", member.getUser(), permission.name(), channel.getName());
		}
		catch(final HierarchyException | InsufficientPermissionException e){
			Log.getLogger(member.getGuild()).warn("Cannot remove permission from {} in {}", member.getUser(), channel.getName());
		}
		catch(final Exception e){
			Log.getLogger(member.getGuild()).warn("Error removing permission from {} in {}", member.getUser(), channel.getName(), e);
		}
	}
	
	/**
	 * Allow a person for members.
	 *
	 * @param members    The members concerned.
	 * @param channel    The channel it'll apply to.
	 * @param permission The permission to give.
	 */
	public static void allowPermission(final List<Member> members, final GuildChannel channel, final Permission permission){
		members.forEach(member -> allowPermission(member, channel, permission));
	}
	
	/**
	 * Allow a person for a member.
	 *
	 * @param member     The member concerned.
	 * @param channel    The channel it'll apply to.
	 * @param permission The permission to give.
	 */
	public static void allowPermission(final Member member, final GuildChannel channel, final Permission permission){
		try{
			channel.putPermissionOverride(member).setAllow(permission).queue();
			Log.getLogger(member.getGuild()).info("{} now have permission {} on {}", member.getUser(), permission.getName(), channel.getName());
		}
		catch(final HierarchyException | InsufficientPermissionException e){
			Log.getLogger(member.getGuild()).warn("Cannot give permission to {} in {}", member.getUser(), channel.getName());
		}
		catch(final Exception e){
			Log.getLogger(member.getGuild()).warn("Error giving permission to {} in {}", member.getUser(), channel.getName(), e);
		}
	}
	
	/**
	 * Sends embeds to a channel.
	 *
	 * @param channel The channel to send to.
	 * @param embeds  The embeds to send.
	 *
	 * @return The messages sent.
	 */
	public static List<Message> getMessage(final TextChannel channel, final List<MessageEmbed> embeds){
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
	public static Message getMessage(final TextChannel channel, final MessageEmbed embed){
		Log.getLogger(channel.getGuild()).info("Sent message to {} : {}", channel.getName(), embed);
		return channel.sendMessage(embed).complete();
	}
	
	public static void deleteMessageById(final long channelId, final long messageId){
		Optional.ofNullable(Main.getJDA().getTextChannelById(channelId)).ifPresent(channel -> channel.deleteMessageById(messageId).queue());
	}
}
