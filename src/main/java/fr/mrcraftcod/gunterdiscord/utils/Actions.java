package fr.mrcraftcod.gunterdiscord.utils;

import fr.mrcraftcod.gunterdiscord.Main;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.io.InputStream;
import java.util.List;
import java.util.function.Consumer;
import static fr.mrcraftcod.gunterdiscord.utils.Utilities.getRole;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-04-15
 */
public class Actions
{
	public static final Consumer<Message> PIN_MESSAGE = m -> m.pin().queue();
	
	public static void reply(MessageReceivedEvent event, String text)
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
	
	public static void reply(MessageReceivedEvent event, String format, Object... args)
	{
		reply(event, String.format(format, args));
	}
	
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
			}
			else
				Log.error("Access denied to text channel: " + channel.getAsMention());
		}
		else
			Log.warning("Cannot send message to null channel : " + text);
	}
	
	public static void sendMessage(TextChannel channel, String text)
	{
		sendMessage(channel, null, text);
	}
	
	public static void sendMessage(TextChannel channel, String format, Object... args)
	{
		sendMessage(channel, String.format(format, args));
	}
	
	public static void sendMessage(TextChannel channel, Consumer<Message> onDone, String format, Object... args)
	{
		sendMessage(channel, onDone, String.format(format, args));
	}
	
	public static void sendMessage(PrivateChannel channel, String text)
	{
		if(channel != null)
			channel.sendMessage(text).queue();
		else
			Log.warning("Cannot send private message to null channel : " + text);
	}
	
	public static void sendMessage(long channelID, String text)
	{
		sendMessage(Main.getJDA().getTextChannelById(channelID), text);
	}
	
	public static void replyPrivate(User user, String text)
	{
		if(user != null)
			user.openPrivateChannel().queue(c -> sendMessage(c, text));
		else
			Log.warning("Sent private message to null user : " + text);
	}
	
	public static void replyPrivate(User user, String format, Object... args)
	{
		replyPrivate(user, String.format(format, args));
	}
	
	public static void giveRole(Guild guild, User user, Roles role)
	{
		List<Role> roles = getRole(guild, role);
		if(roles.size() > 0)
			giveRole(guild, user, roles);
	}
	
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
			Log.warning("User/Role not found", e);
		}
	}
	
	public static void giveRole(Guild guild, User user, List<Role> roles)
	{
		for(Role role : roles)
			giveRole(guild, user, role);
	}
	
	public static void removeRole(Guild guild, User user, Roles role)
	{
		try
		{
			//noinspection ConstantConditions
			guild.getController().removeRolesFromMember(guild.getMember(user), getRole(guild, role.getRole())).queue();
			Log.info("Removed role " + role + " from " + getUserToLog(user));
		}
		catch(IllegalArgumentException e)
		{
			Log.warning("User/Role not found", e);
		}
	}
	
	public static void removeRole(Guild guild, User user, Role role)
	{
		try
		{
			//noinspection ConstantConditions
			guild.getController().removeSingleRoleFromMember(guild.getMember(user), role).queue();
			Log.info("Removed role " + role + " from " + getUserToLog(user));
		}
		catch(IllegalArgumentException e)
		{
			Log.warning("User/Role not found", e);
		}
	}
	
	public static void removeRole(Member member, Role role)
	{
		removeRole(member.getGuild(), member.getUser(), role);
	}
	
	public static void removeRole(Member member, List<Role> roles)
	{
		roles.forEach(r -> removeRole(member, r));
	}
	
	public static String getUserToLog(User user)
	{
		return user == null ? "NULL" : (user.getName() + "#" + user.getDiscriminator() + " (" + user.getIdLong() + ")");
	}
	
	public static void deleteMessage(Message message)
	{
		message.delete().queue();
	}
	
	public static void sendFile(TextChannel channel, String resource, String name)
	{
		sendFile(channel, Main.class.getResourceAsStream(resource), name);
	}
	
	public static void sendFile(TextChannel channel, InputStream stream, String name)
	{
		if(channel != null)
			channel.sendFile(stream, name).queue();
	}
}
