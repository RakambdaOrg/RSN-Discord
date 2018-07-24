package fr.mrcraftcod.gunterdiscord.utils;

import fr.mrcraftcod.gunterdiscord.Main;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.GenericMessageEvent;
import net.dv8tion.jda.core.exceptions.HierarchyException;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
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
public class Actions
{
	public static final Consumer<Message> PIN_MESSAGE = m -> m.pin().queue();
	
	/**
	 * Reply to a message.
	 *
	 * @param event  The message event.
	 * @param format The message format.
	 * @param args   The message parameters.
	 */
	public static void reply(GenericMessageEvent event, String format, Object... args)
	{
		reply(event, String.format(format, args));
	}
	
	/**
	 * Reply to a message.
	 *
	 * @param event The message event.
	 * @param text  The text to send.
	 */
	@SuppressWarnings("Duplicates")
	public static void reply(GenericMessageEvent event, String text)
	{
		switch(event.getChannelType())
		{
			case PRIVATE:
				sendMessage(event.getPrivateChannel(), text);
				break;
			case TEXT:
				sendMessage(event.getTextChannel(), text);
				break;
		}
	}
	
	/**
	 * Send a message to a channel.
	 *
	 * @param channel The channel to send to.
	 * @param text    The message to send.
	 */
	public static void sendMessage(PrivateChannel channel, String text)
	{
		if(channel != null)
		{
			if(channel.getUser().isBot())
				Log.info("Cannot send private message to bot " + Actions.getUserToLog(channel.getUser()) + " : " + text);
			else
			{
				channel.sendMessage(text).queue();
				Log.info("Sent private message to " + getUserToLog(channel.getUser()) + " : " + text);
			}
		}
		else
			Log.warning("Cannot send private message to null channel : %s", text);
	}
	
	/**
	 * Send a message to a channel.
	 *
	 * @param channel The channel to send to.
	 * @param text    The message to send.
	 */
	public static void sendMessage(TextChannel channel, String text)
	{
		sendMessage(channel, null, text);
	}
	
	/**
	 * Get a user in a readable way.
	 *
	 * @param user The user to print.
	 *
	 * @return The string representing the user.
	 */
	public static String getUserToLog(User user)
	{
		return user == null ? "NULL" : (user.getName() + "#" + user.getDiscriminator() + " (" + user.getIdLong() + ")");
	}
	
	/**
	 * Send a message to a channel.
	 *
	 * @param channel The channel to send to.
	 * @param onDone  The action to do when done.
	 * @param text    The message to send.
	 */
	@SuppressWarnings("Duplicates")
	public static void sendMessage(TextChannel channel, Consumer<Message> onDone, String text)
	{
		if(channel != null)
		{
			if(channel.canTalk())
			{
				if(onDone != null)
					channel.sendMessage(text).queue(onDone);
				else
					channel.sendMessage(text).queue();
				Log.info("Sent message to " + channel.getName() + " : " + text);
			}
			else
				Log.error("Access denied to text channel: %s", channel.getAsMention());
		}
		else
			Log.warning("Cannot send message to null channel : %s", text);
	}
	
	/**
	 * Send a message to a channel.
	 *
	 * @param channel The channel to send to.
	 * @param format  The format fo the message.
	 * @param args    The message parameters.
	 */
	public static void sendMessage(TextChannel channel, String format, Object... args)
	{
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
	public static void sendMessage(TextChannel channel, Consumer<Message> onDone, String format, Object... args)
	{
		sendMessage(channel, onDone, String.format(format, args));
	}
	
	/**
	 * Send a message to a channel.
	 *
	 * @param channelID The channel to send to.
	 * @param text      The message to send.
	 */
	public static void sendMessage(long channelID, String text)
	{
		sendMessage(Main.getJDA().getTextChannelById(channelID), text);
	}
	
	/**
	 * Send a message to a user.
	 *
	 * @param user   The user to send to.
	 * @param format The format fo the message.
	 * @param args   The message parameters.
	 */
	public static void replyPrivate(User user, String format, Object... args)
	{
		replyPrivate(user, String.format(format, args));
	}
	
	/**
	 * Send a message to a user.
	 *
	 * @param user The user to send to.
	 * @param text The message to send.
	 */
	public static void replyPrivate(User user, String text)
	{
		if(user != null)
			user.openPrivateChannel().queue(c -> sendMessage(c, text));
		else
			Log.warning("Sent private message to null user : %s", text);
	}
	
	/**
	 * Remove a role from a user.
	 *
	 * @param guild The guild the role is in.
	 * @param user  The user to remove the role from.
	 * @param role  The role to remove.
	 */
	public static void removeRole(Guild guild, User user, Role role)
	{
		removeRole(guild.getMember(user), role);
	}
	
	/**
	 * Remove a role from a user.
	 *
	 * @param user The user to remove the role from.
	 * @param role The role to remove.
	 */
	public static void removeRole(User user, Role role)
	{
		removeRole(role.getGuild().getMember(user), role);
	}
	
	/**
	 * Remove a role from a user.
	 *
	 * @param member The user to remove the role from.
	 * @param role   The role to remove.
	 */
	public static void removeRole(Member member, Role role)
	{
		try
		{
			//noinspection ConstantConditions
			member.getGuild().getController().removeSingleRoleFromMember(member, role).queue();
			Log.info("Removed role " + role + " from " + getUserToLog(member.getUser()));
		}
		catch(IllegalArgumentException e)
		{
			Log.warning(e, "User/Role not found");
		}
	}
	
	/**
	 * Remove roles from a user.
	 *
	 * @param member The user to remove the roles from.
	 * @param roles  The roles to remove.
	 */
	public static void removeRole(Member member, List<Role> roles)
	{
		roles.forEach(r -> removeRole(member, r));
	}
	
	/**
	 * Delete a message.
	 *
	 * @param message The message to delete.
	 */
	public static void deleteMessage(Message message)
	{
		message.delete().queue();
		Log.info("Deleted message from " + getUserToLog(message.getAuthor()) + " : " + message.getContentRaw());
	}
	
	/**
	 * Send a file to a channel.
	 *
	 * @param channel  The channel to send to.
	 * @param resource The resource to send.
	 * @param name     The name of the file.
	 */
	public static void sendFile(TextChannel channel, String resource, String name)
	{
		sendFile(channel, Main.class.getResourceAsStream(resource), name);
	}
	
	/**
	 * Send a file to a channel.
	 *
	 * @param channel The channel to send to.
	 * @param stream  The data to send.
	 * @param name    The name of the file.
	 */
	public static void sendFile(TextChannel channel, InputStream stream, String name)
	{
		if(channel != null)
		{
			channel.sendFile(stream, name).queue();
			Log.info("Sent file " + name + " to " + channel.getName());
		}
		else
			Log.warning("Tried to send file %s to null channel", name);
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
	public static Message getMessage(TextChannel channel, String format, Object... args)
	{
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
	public static Message getMessage(TextChannel channel, String text)
	{
		if(channel != null)
		{
			Log.info("Sent message to " + channel.getName() + " : " + text);
			return channel.sendMessage(text).complete();
		}
		return null;
	}
	
	/**
	 * Send a message and gets it.
	 *
	 * @param channel the channel to send to.
	 * @param embed   The message to send.
	 *
	 * @return The message sent or null fi there was a problem.
	 */
	public static Message getMessage(TextChannel channel, MessageEmbed embed)
	{
		if(channel != null)
		{
			Log.info("Sent message to " + channel.getName() + " : " + embed);
			return channel.sendMessage(embed).complete();
		}
		return null;
	}
	
	/**
	 * Send a file to a channel.
	 *
	 * @param channel The channel to send to.
	 * @param file    The file to send.
	 */
	public static void sendFile(TextChannel channel, File file)
	{
		try
		{
			sendFile(channel, new FileInputStream(file), file.getName());
		}
		catch(FileNotFoundException e)
		{
			Log.error(e, "Error sending file %s", file.getAbsolutePath());
		}
	}
	
	/**
	 * Give roles to the user.
	 *
	 * @param member The member.
	 * @param roles  The roles IDs.
	 */
	public static void giveRole(Member member, List<Long> roles)
	{
		giveRole(member.getGuild(), member.getUser(), roles.stream().map(r -> getRoleByID(member.getGuild(), r)).collect(Collectors.toList()));
	}
	
	/**
	 * Give roles to a user.
	 *
	 * @param guild The guild the role is in.
	 * @param user  The user to set the role to.
	 * @param roles The roles to set.
	 */
	public static void giveRole(Guild guild, User user, List<Role> roles)
	{
		for(Role role : roles)
			giveRole(guild, user, role);
	}
	
	/**
	 * Get a role by its ID.
	 *
	 * @param guild The guild the role is in.
	 * @param role  The ID of the role.
	 *
	 * @return The role or null if not found.
	 */
	public static Role getRoleByID(Guild guild, Long role)
	{
		return guild.getRoleById(role);
	}
	
	/**
	 * Give a role to a user.
	 *
	 * @param guild The guild the role is in.
	 * @param user  The user to set the role to.
	 * @param role  The role to set.
	 */
	public static void giveRole(Guild guild, User user, Role role)
	{
		try
		{
			Member member = guild.getMember(user);
			if(member.getRoles().contains(role))
			{
				Log.info(getUserToLog(user) + " already have role " + role);
			}
			else
			{
				//noinspection ConstantConditions
				guild.getController().addSingleRoleToMember(guild.getMember(user), role).queue();
				Log.info("Added role " + role + " to " + getUserToLog(user));
			}
		}
		catch(IllegalArgumentException e)
		{
			Log.warning(e, "User/Role not found " + role);
		}
		catch(Exception e)
		{
			Log.error(e, "Error giving role %s to %s", role, getUserToLog(user));
		}
	}
	
	/**
	 * Reply to an event?
	 *
	 * @param event The event.
	 * @param embed The message to send.
	 */
	@SuppressWarnings("Duplicates")
	public static void reply(GenericMessageEvent event, MessageEmbed embed)
	{
		switch(event.getChannelType())
		{
			case PRIVATE:
				sendMessage(event.getPrivateChannel(), embed);
				break;
			case TEXT:
				sendMessage(event.getTextChannel(), embed);
				break;
		}
	}
	
	/**
	 * Send a message to a channel.
	 *
	 * @param channel The channel to send to.
	 * @param embed   The message to send.
	 */
	public static void sendMessage(PrivateChannel channel, MessageEmbed embed)
	{
		if(channel != null)
		{
			channel.sendMessage(embed).queue();
			Log.info("Sent private message to " + getUserToLog(channel.getUser()) + " : " + getEmbedForLog(embed));
		}
		else
			Log.warning("Cannot send private message to null channel : %s", getEmbedForLog(embed));
	}
	
	/**
	 * Send a message to a channel.
	 *
	 * @param channel The channel to send to.
	 * @param embed   The message to send.
	 */
	public static void sendMessage(TextChannel channel, MessageEmbed embed)
	{
		sendMessage(channel, null, embed);
	}
	
	/**
	 * Transform an embed into text.
	 *
	 * @param embed The embed.
	 *
	 * @return The text.
	 */
	private static String getEmbedForLog(MessageEmbed embed)
	{
		StringBuilder builder = new StringBuilder("Embed " + embed.hashCode());
		builder.append("\n").append("Author: ").append(embed.getAuthor() == null ? "<NONE>" : embed.getAuthor().getName());
		builder.append("\n").append("Title: ").append(embed.getTitle());
		builder.append("\n").append("Description: ").append(embed.getDescription());
		builder.append("\n").append("Color: ").append(embed.getColor());
		embed.getFields().forEach(f -> {
			builder.append("\n").append("FIELD:");
			builder.append("\n\t").append("Name: ").append(f.getName());
			builder.append("\n\t").append("Value: ").append(f.getValue());
		});
		return builder.toString();
	}
	
	/**
	 * Send a message to a channel.
	 *
	 * @param channel The channel to send to.
	 * @param onDone  The action to do when done.
	 * @param embed   The message to send.
	 */
	@SuppressWarnings("Duplicates")
	public static void sendMessage(TextChannel channel, Consumer<Message> onDone, MessageEmbed embed)
	{
		if(channel != null)
		{
			if(channel.canTalk())
			{
				if(onDone != null)
					channel.sendMessage(embed).queue(onDone);
				else
					channel.sendMessage(embed).queue();
				Log.info("Sent message to " + channel.getName() + " : " + getEmbedForLog(embed));
			}
			else
				Log.error("Access denied to text channel: %s, when sending: %s", channel.getAsMention(), getEmbedForLog(embed));
		}
		else
			Log.warning("Cannot send message to null channel : %s", getEmbedForLog(embed));
	}
	
	/**
	 * Send a message to a channel.
	 *
	 * @param channel The channel to send to.
	 * @param embeds  The messages to send.
	 */
	public static void sendMessage(TextChannel channel, List<MessageEmbed> embeds)
	{
		embeds.forEach(e -> sendMessage(channel, e));
	}
	
	/**
	 * Deafen members.
	 *
	 * @param members The member to set deaf.
	 * @param state   True if deaf, false is not deaf.
	 */
	public static void deafen(List<Member> members, boolean state)
	{
		members.forEach(m -> deafen(m, state));
	}
	
	/**
	 * Deafen a member.
	 *
	 * @param member The member to set deaf.
	 * @param state  True if deaf, false is not deaf.
	 */
	private static void deafen(Member member, boolean state)
	{
		try
		{
			member.getGuild().getController().setDeafen(member, state).queue();
			Log.info("Member " + Actions.getUserToLog(member.getUser()) + " is now " + (state ? "" : "un") + "deaf");
		}
		catch(HierarchyException | InsufficientPermissionException e)
		{
			Log.warning("Cannot %sdeaf member %s", (state ? "" : "un"), Actions.getUserToLog(member.getUser()));
		}
		catch(Exception e)
		{
			Log.error(e, "Error trying to %sdeafen member %s", (state ? "" : "un"), getUserToLog(member.getUser()));
		}
	}
	
	/**
	 * Deny a person for members.
	 *
	 * @param members    The members concerned.
	 * @param channel    The channel it'll apply to.
	 * @param permission The permission to remove.
	 */
	public static void denyPermission(List<Member> members, Channel channel, Permission permission)
	{
		members.forEach(m -> denyPermission(m, channel, permission));
	}
	
	/**
	 * Deny a person for a member.
	 *
	 * @param member     The member concerned.
	 * @param channel    The channel it'll apply to.
	 * @param permission The permission to remove.
	 */
	public static void denyPermission(Member member, Channel channel, Permission permission)
	{
		try
		{
			channel.putPermissionOverride(member).setDeny(permission).queue();
			Log.info(Actions.getUserToLog(member.getUser()) + " no longer have permission " + permission.name() + " on " + channel.getName());
		}
		catch(HierarchyException | InsufficientPermissionException e)
		{
			Log.warning("Cannot remove permission from %s in %s", Actions.getUserToLog(member.getUser()), channel.getName());
		}
		catch(Exception e)
		{
			Log.warning("Error removing permission from %s in %s", Actions.getUserToLog(member.getUser()), channel.getName());
		}
	}
	
	/**
	 * Allow a person for members.
	 *
	 * @param members    The members concerned.
	 * @param channel    The channel it'll apply to.
	 * @param permission The permission to give.
	 */
	public static void allowPermission(List<Member> members, Channel channel, Permission permission)
	{
		members.forEach(m -> allowPermission(m, channel, permission));
	}
	
	/**
	 * Allow a person for a member.
	 *
	 * @param member     The member concerned.
	 * @param channel    The channel it'll apply to.
	 * @param permission The permission to give.
	 */
	public static void allowPermission(Member member, Channel channel, Permission permission)
	{
		try
		{
			channel.putPermissionOverride(member).setAllow(permission).queue();
			Log.info(Actions.getUserToLog(member.getUser()) + " now have permission " + permission.name() + " on " + channel.getName());
		}
		catch(HierarchyException | InsufficientPermissionException e)
		{
			Log.warning("Cannot give permission to %s in %s", Actions.getUserToLog(member.getUser()), channel.getName());
		}
		catch(Exception e)
		{
			Log.warning("Error giving permission to %s in %s", Actions.getUserToLog(member.getUser()), channel.getName());
		}
	}
	
	public static List<Message> getMessage(TextChannel channel, List<MessageEmbed> embeds)
	{
		return embeds.stream().map(embed -> getMessage(channel, embed)).collect(Collectors.toList());
	}
}
