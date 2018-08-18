package fr.mrcraftcod.gunterdiscord.listeners;

import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceGuildMuteEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.core.events.self.SelfUpdateNameEvent;
import net.dv8tion.jda.core.events.user.update.UserUpdateNameEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-05-06
 */
public class LogListener extends ListenerAdapter{
	@Override
	public void onUserUpdateName(final UserUpdateNameEvent event){
		super.onUserUpdateName(event);
		try{
			getLogger(null).info("User {} changed name of {} `{}` to `{}`", Utilities.getUserToLog(event.getUser()), Utilities.getUserToLog(event.getEntity()), event.getOldName(), event.getNewName());
		}
		catch(final NullPointerException ignored){
		}
		catch(final Exception e){
			getLogger(null).error("", e);
		}
	}
	
	@Override
	public void onSelfUpdateName(final SelfUpdateNameEvent event){
		super.onSelfUpdateName(event);
		try{
			getLogger(null).info("User {} changed name `{}` to `{}`", Utilities.getUserToLog(event.getEntity()), event.getOldName(), event.getNewName());
		}
		catch(final NullPointerException ignored){
		}
		catch(final Exception e){
			getLogger(null).error("", e);
		}
	}
	
	@Override
	public void onMessageReactionAdd(final MessageReactionAddEvent event){
		super.onMessageReactionAdd(event);
		try{
			event.getReaction().getTextChannel().getMessageById(event.getMessageIdLong()).queue(m -> getLogger(event.getGuild()).info("New reaction {} from `{}` in {} on `{}` whose author is {}", event.getReaction().getReactionEmote().getName(), Utilities.getUserToLog(event.getUser()), event.getReaction().getTextChannel().getName(), m.getContentRaw().replace("\n", "{n}"), Utilities.getUserToLog(m.getAuthor())));
		}
		catch(final NullPointerException ignored){
		}
		catch(final Exception e){
			getLogger(event.getGuild()).error("", e);
		}
	}
	
	@Override
	public void onMessageReactionRemove(final MessageReactionRemoveEvent event){
		super.onMessageReactionRemove(event);
		try{
			event.getReaction().getTextChannel().getMessageById(event.getMessageIdLong()).queue(m -> getLogger(event.getGuild()).info("Reaction {} removed by `{}` in {} on `{}` whose author is {}", event.getReaction().getReactionEmote().getName(), Utilities.getUserToLog(event.getUser()), event.getReaction().getTextChannel().getName(), m.getContentRaw().replace("\n", "{n}"), Utilities.getUserToLog(m.getAuthor())));
		}
		catch(final NullPointerException ignored){
		}
		catch(final Exception e){
			getLogger(event.getGuild()).error("", e);
		}
	}
	
	@Override
	public void onGuildVoiceGuildMute(final GuildVoiceGuildMuteEvent event){
		super.onGuildVoiceGuildMute(event);
		if(event.getMember().getUser().getIdLong() == event.getJDA().getSelfUser().getIdLong()){
			getLogger(event.getGuild()).info("Unmuting bot");
			event.getGuild().getController().setMute(event.getMember(), false).queue();
			event.getGuild().getController().setDeafen(event.getMember(), false).queue();
		}
	}
}
