package fr.mrcraftcod.gunterdiscord.utils;

import fr.mrcraftcod.gunterdiscord.Main;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import static fr.mrcraftcod.gunterdiscord.utils.Utilities.getRole;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-04-15
 */
public class Actions
{
	public static Message reply(MessageReceivedEvent event, String text)
	{
		return sendMessage(event.getMessage().getTextChannel(), text);
	}
	
	public static Message sendMessage(TextChannel channel, String text)
	{
		if(channel != null)
		{
			if(channel.canTalk())
				return channel.sendMessage(text).complete();
			else
				reportError("Access denied to text channel: " + channel.getAsMention());
		}
		return null;
	}
	
	public static Message sendMessage(PrivateChannel channel, String text)
	{
		if(channel != null)
			return channel.sendMessage(text).complete();
		return null;
	}
	
	private static void reportError(String text)
	{
		//TODO
	}
	
	public static Message sendMessage(long channelID, String text)
	{
		return sendMessage(Main.getJDA().getTextChannelById(channelID), text);
	}
	
	public static void replyPrivate(User user, String text)
	{
		if(user != null)
		{
			user.openPrivateChannel().queue(channel -> channel.sendMessage(text).queue());
			Log.info("Sent private message to " + getUserToLog(user) + ": " + text);
		}
		else
			Log.warning("Sent message to null");
	}
	
	public static void giveRole(Guild guild, User user, Roles role)
	{
		try
		{
			//noinspection ConstantConditions
			guild.getController().addSingleRoleToMember(guild.getMember(user), getRole(Main.getJDA(), role.getRole())).queue();
			Log.info("Added role " + role + " to " + getUserToLog(user));
		}
		catch(IllegalArgumentException e)
		{
			Log.warning("User/Role not found", e);
		}
	}
	
	public static void removeRole(Guild guild, User user, Roles role)
	{
		try
		{
			//noinspection ConstantConditions
			guild.getController().removeSingleRoleFromMember(guild.getMember(user), getRole(Main.getJDA(), role.getRole())).queue();
			Log.info("Removed role " + role + " from " + getUserToLog(user));
		}
		catch(IllegalArgumentException e)
		{
			Log.warning("User/Role not found", e);
		}
	}
	
	public static String getUserToLog(User user)
	{
		return user == null ? "NULL" : (user.getName() + "#" + user.getDiscriminator() + " (" + user.getIdLong() + ")");
	}
}
