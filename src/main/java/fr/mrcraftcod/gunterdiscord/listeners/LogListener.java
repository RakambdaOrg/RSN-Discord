package fr.mrcraftcod.gunterdiscord.listeners;

import fr.mrcraftcod.gunterdiscord.settings.configs.EnableNameChangeLimitConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.MegaWarnRoleConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.NameLastChangeConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.RemoveRoleConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.core.entities.Guild;
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
			for(Guild guild : event.getEntity().getMutualGuilds()){
				if(new EnableNameChangeLimitConfig(guild).getObject(false)){
					var config = new NameLastChangeConfig(guild);
					var diff = System.currentTimeMillis() - config.getAsMap().getOrDefault(event.getEntity().getIdLong(), 0L);
					if(diff < 3600000){
						var warnRole = new MegaWarnRoleConfig(guild).getObject();
						var removeRoleConfig = new RemoveRoleConfig(guild);
						var currentRoleRemove = removeRoleConfig.getAsMap().keySet().stream().filter(k -> k == event.getEntity().getIdLong()).map(removeRoleConfig::getValue).map(map -> map.getOrDefault(warnRole.getIdLong(), 0L)).findFirst().orElse(0L);
						Actions.giveRole(guild, event.getEntity(), warnRole);
						removeRoleConfig.addValue(event.getEntity().getIdLong(), warnRole.getIdLong(), Math.max(currentRoleRemove, System.currentTimeMillis() + 6 * 60 * 60 * 1000L));
						Actions.replyPrivate(guild, event.getEntity(), "Vous avez été warn dans le serveur `%s` car vous avez changé de nom trop souvent.", guild.getName());
					}
					config.addValue(event.getEntity().getIdLong(), System.currentTimeMillis());
				}
			}
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
