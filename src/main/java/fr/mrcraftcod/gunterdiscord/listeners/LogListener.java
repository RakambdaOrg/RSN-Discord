package fr.mrcraftcod.gunterdiscord.listeners;

import fr.mrcraftcod.gunterdiscord.commands.EmotesCommand;
import fr.mrcraftcod.gunterdiscord.commands.TempParticipationCommand;
import fr.mrcraftcod.gunterdiscord.settings.configs.done.EmotesParticipationConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.done.MembersParticipationConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.done.NoXPChannelsConfig;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildMuteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.events.self.SelfUpdateNameEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateNameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-05-06
 */
public class LogListener extends ListenerAdapter{
	@Override
	public void onUserUpdateName(@Nonnull final UserUpdateNameEvent event){
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
	public void onSelfUpdateName(@Nonnull final SelfUpdateNameEvent event){
		super.onSelfUpdateName(event);
		try{
			getLogger(null).debug("User {} changed name `{}` to `{}`", event.getEntity(), event.getOldName(), event.getNewName());
		}
		catch(final NullPointerException ignored){
		}
		catch(final Exception e){
			getLogger(null).error("", e);
		}
	}
	
	@Override
	public void onGuildMessageReceived(@Nonnull final GuildMessageReceivedEvent event){
		super.onGuildMessageReceived(event);
		try{
			if(!event.getAuthor().equals(event.getJDA().getSelfUser())){
				final var now = LocalDate.now();
				if(!new NoXPChannelsConfig(event.getGuild()).getAsList().map(list -> list.contains(event.getChannel())).orElse(false)){
					final var participation = new MembersParticipationConfig(event.getGuild());
					participation.getAsMap().ifPresent(participationMap -> {
						final var todayKey = TempParticipationCommand.DF.format(now);
						if(participationMap.containsKey(todayKey)){
							participation.addValue(todayKey, event.getAuthor().getIdLong(), participationMap.get(todayKey).getOrDefault(event.getAuthor().getIdLong(), 0L) + 1);
						}
						else{
							participation.addValue(todayKey, event.getAuthor().getIdLong(), 1L);
						}
					});
				}
				final var emotes = new EmotesParticipationConfig(event.getGuild());
				emotes.getAsMap().ifPresent(emotesMap -> {
					final var weekKey = EmotesCommand.DF.format(now);
					if(!emotesMap.containsKey(weekKey)){
						emotes.addValue(weekKey);
					}
					event.getMessage().getEmotes().stream().map(Emote::getName).forEach(id -> emotes.addValue(weekKey, id, emotesMap.get(weekKey).getOrDefault(id, 0L) + 1));
				});
			}
		}
		catch(final Exception e){
			getLogger(event.getGuild()).error("", e);
		}
	}
	
	@Override
	public void onGuildMessageReactionAdd(@Nonnull final GuildMessageReactionAddEvent event){
		super.onGuildMessageReactionAdd(event);
		try{
			Optional.ofNullable(event.getReaction().getTextChannel()).map(TextChannel::getHistory).map(history -> history.getMessageById(event.getMessageIdLong())).ifPresent(message -> getLogger(event.getGuild()).debug("New reaction {} from `{}` in {} on `{}` whose author is {}", event.getReaction().getReactionEmote().getName(), event.getUser(), event.getReaction().getTextChannel().getName(), message.getContentRaw().replace("\n", "{n}"), message.getAuthor()));
		}
		catch(final NullPointerException ignored){
		}
		catch(final Exception e){
			getLogger(event.getGuild()).error("", e);
		}
	}
	
	@Override
	public void onGuildMessageReactionRemove(@Nonnull final GuildMessageReactionRemoveEvent event){
		super.onGuildMessageReactionRemove(event);
		try{
			Optional.ofNullable(event.getReaction().getTextChannel()).map(TextChannel::getHistory).map(history -> history.getMessageById(event.getMessageIdLong())).ifPresent(message -> getLogger(event.getGuild()).debug("Reaction {} removed by `{}` in {} on `{}` whose author is {}", event.getReaction().getReactionEmote().getName(), event.getUser(), event.getReaction().getTextChannel().getName(), message.getContentRaw().replace("\n", "{n}"), message.getAuthor()));
		}
		catch(final NullPointerException ignored){
		}
		catch(final Exception e){
			getLogger(event.getGuild()).error("", e);
		}
	}
	
	@Override
	public void onGuildVoiceGuildMute(@Nonnull final GuildVoiceGuildMuteEvent event){
		super.onGuildVoiceGuildMute(event);
		if(Objects.equals(event.getMember().getUser(), event.getJDA().getSelfUser())){
			getLogger(event.getGuild()).info("Unmuting bot");
			event.getGuild().mute(event.getMember(), false).queue();
			event.getGuild().deafen(event.getMember(), false).queue();
		}
	}
}
