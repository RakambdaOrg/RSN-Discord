package fr.mrcraftcod.gunterdiscord.listeners;

import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Log;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.core.events.self.SelfUpdateNameEvent;
import net.dv8tion.jda.core.events.user.update.UserUpdateNameEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-05-06
 */
public class LogListener extends ListenerAdapter
{
	@Override
	public void onUserUpdateName(UserUpdateNameEvent event)
	{
		super.onUserUpdateName(event);
		try
		{
			Log.info("User " + Actions.getUserToLog(event.getUser()) + " changed name of " + Actions.getUserToLog(event.getEntity()) + " `" + event.getOldName() + "` to `" + event.getNewName() + "`");
		}
		catch(NullPointerException ignored)
		{
		}
		catch(Exception e)
		{
			Log.error("", e);
		}
	}
	
	@Override
	public void onSelfUpdateName(SelfUpdateNameEvent event)
	{
		super.onSelfUpdateName(event);
		try
		{
			Log.info("User " + Actions.getUserToLog(event.getSelfUser()) + " changed name `" + event.getOldName() + "` to `" + event.getNewName() + "`");
		}
		catch(NullPointerException ignored)
		{
		}
		catch(Exception e)
		{
			Log.error("", e);
		}
	}
	
	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event)
	{
		super.onMessageReactionAdd(event);
		try
		{
			Log.info("New reaction " + event.getReaction().getReactionEmote().getName() + " from `" + Actions.getUserToLog(event.getUser()) + "` in " + event.getReaction().getTextChannel().getName() + " on `" + event.getReaction().getTextChannel().getMessageById(event.getMessageIdLong()).complete().getContentRaw().replace("\n", "{n}") + "`");
		}
		catch(NullPointerException ignored)
		{
		}
		catch(Exception e)
		{
			Log.error("", e);
		}
	}
	
	@Override
	public void onMessageReactionRemove(MessageReactionRemoveEvent event)
	{
		super.onMessageReactionRemove(event);
		try
		{
			Log.info("Reaction " + event.getReaction().getReactionEmote().getName() + " removed by `" + Actions.getUserToLog(event.getUser()) + "` in " + event.getReaction().getTextChannel().getName() + " on `" + event.getReaction().getTextChannel().getMessageById(event.getMessageIdLong()).complete().getContentRaw().replace("\n", "{n}") + "`");
		}
		catch(NullPointerException ignored)
		{
		}
		catch(Exception e)
		{
			Log.error("", e);
		}
	}
}
