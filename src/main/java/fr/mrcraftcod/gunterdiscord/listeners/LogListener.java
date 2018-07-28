package fr.mrcraftcod.gunterdiscord.listeners;

import fr.mrcraftcod.gunterdiscord.utils.Log;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceGuildMuteEvent;
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
			Log.info(null, "User {} changed name of {} `{}` to `{}`", Utilities.getUserToLog(event.getUser()), Utilities.getUserToLog(event.getEntity()), event.getOldName(), event.getNewName());
		}
		catch(NullPointerException ignored)
		{
		}
		catch(Exception e)
		{
			Log.error(null, "", e);
		}
	}
	
	@Override
	public void onSelfUpdateName(SelfUpdateNameEvent event)
	{
		super.onSelfUpdateName(event);
		try
		{
			Log.info(null, "User {} changed name `{}` to `{}`", Utilities.getUserToLog(event.getEntity()), event.getOldName(), event.getNewName());
		}
		catch(NullPointerException ignored)
		{
		}
		catch(Exception e)
		{
			Log.error(null, "", e);
		}
	}
	
	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event)
	{
		super.onMessageReactionAdd(event);
		try
		{
			event.getReaction().getTextChannel().getMessageById(event.getMessageIdLong()).queue(m -> Log.info(event.getGuild(), "New reaction {} from `{}` in {} on `{}` whose author is {}", event.getReaction().getReactionEmote().getName(), Utilities.getUserToLog(event.getUser()), event.getReaction().getTextChannel().getName(), m.getContentRaw().replace("\n", "{n}"), Utilities.getUserToLog(m.getAuthor())));
		}
		catch(NullPointerException ignored)
		{
		}
		catch(Exception e)
		{
			Log.error(event.getGuild(), "", e);
		}
	}
	
	@Override
	public void onMessageReactionRemove(MessageReactionRemoveEvent event)
	{
		super.onMessageReactionRemove(event);
		try
		{
			event.getReaction().getTextChannel().getMessageById(event.getMessageIdLong()).queue(m -> Log.info(event.getGuild(), "Reaction {} removed by `{}` in {} on `{}` whose author is {}", event.getReaction().getReactionEmote().getName(), Utilities.getUserToLog(event.getUser()), event.getReaction().getTextChannel().getName(), m.getContentRaw().replace("\n", "{n}"), Utilities.getUserToLog(m.getAuthor())));
		}
		catch(NullPointerException ignored)
		{
		}
		catch(Exception e)
		{
			Log.error(event.getGuild(), "", e);
		}
	}
	
	@Override
	public void onGuildVoiceGuildMute(GuildVoiceGuildMuteEvent event)
	{
		super.onGuildVoiceGuildMute(event);
		if(event.getMember().getUser().getIdLong() == event.getJDA().getSelfUser().getIdLong())
		{
			Log.info(event.getGuild(), "Unmuting bot");
			event.getGuild().getController().setMute(event.getMember(), false).queue();
			event.getGuild().getController().setDeafen(event.getMember(), false).queue();
		}
	}
}
