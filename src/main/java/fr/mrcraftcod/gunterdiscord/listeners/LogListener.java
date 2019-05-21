package fr.mrcraftcod.gunterdiscord.listeners;

import fr.mrcraftcod.gunterdiscord.commands.EmotesCommand;
import fr.mrcraftcod.gunterdiscord.commands.TempParticipationCommand;
import fr.mrcraftcod.gunterdiscord.settings.configs.*;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildMuteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.events.self.SelfUpdateNameEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateNameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import java.time.LocalDate;
import java.util.Objects;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-05-06
 */
public class LogListener extends ListenerAdapter{
	@Override
	public void onUserUpdateName(@NotNull final UserUpdateNameEvent event){
		super.onUserUpdateName(event);
		try{
			getLogger(null).debug("User {} changed name of {} `{}` to `{}`", event.getUser(), event.getEntity(), event.getOldName(), event.getNewName());
		}
		catch(final NullPointerException ignored){
		}
		catch(final Exception e){
			getLogger(null).error("", e);
		}
	}
	
	@Override
	public void onSelfUpdateName(@NotNull final SelfUpdateNameEvent event){
		super.onSelfUpdateName(event);
		try{
			getLogger(null).debug("User {} changed name `{}` to `{}`", event.getEntity(), event.getOldName(), event.getNewName());
			for(final var guild : event.getEntity().getMutualGuilds()){
				if(new EnableNameChangeLimitConfig(guild).getObject(false)){
					final var config = new NameLastChangeConfig(guild);
					final var diff = System.currentTimeMillis() - config.getAsMap().getOrDefault(event.getEntity().getIdLong(), 0L);
					if(diff < 3600000){
						final var warnRole = new MegaWarnRoleConfig(guild).getObject();
						final var removeRoleConfig = new RemoveRoleConfig(guild);
						final var currentRoleRemove = removeRoleConfig.getAsMap().keySet().stream().filter(key -> Objects.equals(key, event.getEntity().getIdLong())).map(removeRoleConfig::getValue).map(map -> map.getOrDefault(warnRole.getIdLong(), 0L)).findFirst().orElse(0L);
						Actions.giveRole(guild, event.getEntity(), warnRole);
						removeRoleConfig.addValue(event.getEntity().getIdLong(), warnRole.getIdLong(), Math.max(currentRoleRemove, System.currentTimeMillis() + 6 * 60 * 60 * 1000L));
						Actions.replyPrivate(guild, event.getEntity(), "You've been warned in the server `%s` because you changed your name too often.", guild.getName());
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
	public void onGuildMessageReceived(@NotNull final GuildMessageReceivedEvent event){
		super.onGuildMessageReceived(event);
		try{
			if(!event.getAuthor().equals(event.getJDA().getSelfUser())){
				final var now = LocalDate.now();
				if(!new NoXPChannelsConfig(event.getGuild()).getAsList().contains(event.getChannel())){
					final var participation = new MembersParticipationConfig(event.getGuild());
					final var participationMap = participation.getAsMap();
					final var todayKey = TempParticipationCommand.DF.format(now);
					if(participationMap.containsKey(todayKey)){
						participation.addValue(todayKey, event.getAuthor().getIdLong(), participationMap.get(todayKey).getOrDefault(event.getAuthor().getIdLong(), 0L) + 1);
					}
					else{
						participation.addValue(todayKey, event.getAuthor().getIdLong(), 1L);
					}
				}
				final var emotes = new EmotesParticipationConfig(event.getGuild());
				final var emotesMap = emotes.getAsMap();
				final var weekKey = EmotesCommand.DF.format(now);
				if(!emotesMap.containsKey(weekKey)){
					emotes.addValue(weekKey);
				}
				event.getMessage().getEmotes().stream().map(Emote::getName).forEach(id -> emotes.addValue(weekKey, id, emotesMap.get(weekKey).getOrDefault(id, 0L) + 1));
			}
		}
		catch(final Exception e){
			getLogger(event.getGuild()).error("", e);
		}
	}
	
	@Override
	public void onGuildMessageReactionAdd(@NotNull final GuildMessageReactionAddEvent event){
		super.onGuildMessageReactionAdd(event);
		try{
			final var message = event.getReaction().getTextChannel().getHistory().getMessageById(event.getMessageIdLong());
			getLogger(event.getGuild()).debug("New reaction {} from `{}` in {} on `{}` whose author is {}", event.getReaction().getReactionEmote().getName(), event.getUser(), event.getReaction().getTextChannel().getName(), message.getContentRaw().replace("\n", "{n}"), message.getAuthor());
		}
		catch(final NullPointerException ignored){
		}
		catch(final Exception e){
			getLogger(event.getGuild()).error("", e);
		}
	}
	
	@Override
	public void onGuildMessageReactionRemove(@NotNull final GuildMessageReactionRemoveEvent event){
		super.onGuildMessageReactionRemove(event);
		try{
			final var message = event.getReaction().getTextChannel().getHistory().getMessageById(event.getMessageIdLong());
			getLogger(event.getGuild()).debug("Reaction {} removed by `{}` in {} on `{}` whose author is {}", event.getReaction().getReactionEmote().getName(), event.getUser(), event.getReaction().getTextChannel().getName(), message.getContentRaw().replace("\n", "{n}"), message.getAuthor());
		}
		catch(final NullPointerException ignored){
		}
		catch(final Exception e){
			getLogger(event.getGuild()).error("", e);
		}
	}
	
	@Override
	public void onGuildVoiceGuildMute(@NotNull final GuildVoiceGuildMuteEvent event){
		super.onGuildVoiceGuildMute(event);
		if(Objects.equals(event.getMember().getUser(), event.getJDA().getSelfUser())){
			getLogger(event.getGuild()).info("Unmuting bot");
			event.getGuild().getController().setMute(event.getMember(), false).queue();
			event.getGuild().getController().setDeafen(event.getMember(), false).queue();
		}
	}
}
