package fr.mrcraftcod.gunterdiscord.listeners;

import fr.mrcraftcod.gunterdiscord.settings.NewSettings;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildMuteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.events.self.SelfUpdateNameEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateNameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import javax.annotation.Nonnull;
import java.time.DayOfWeek;
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
				if(NewSettings.getConfiguration(event.getGuild()).getNoXpChannels().stream().noneMatch(c -> Objects.equals(c.getChannelId(), event.getChannel().getIdLong()))){
					final var users = NewSettings.getConfiguration(event.getGuild()).getParticipationConfiguration().getUsers(now);
					users.increment(event.getAuthor().getIdLong(), event.getAuthor().getName());
				}

				final var weekKey = now.minusDays(getDaysToRemove(now.getDayOfWeek()));
				final var emotes = NewSettings.getConfiguration(event.getGuild()).getParticipationConfiguration().getEmotes(weekKey);
				event.getMessage().getEmotes().forEach(emote -> emotes.increment(emote.getIdLong(), emote.getName()));
			}
		}
		catch(final Exception e){
			getLogger(event.getGuild()).error("", e);
		}
	}
	
	public static long getDaysToRemove(DayOfWeek dayOfWeek){
		switch(dayOfWeek){
			default:
			case MONDAY:
				return 0;
			case TUESDAY:
				return 1;
			case WEDNESDAY:
				return 2;
			case THURSDAY:
				return 3;
			case FRIDAY:
				return 4;
			case SATURDAY:
				return 5;
			case SUNDAY:
				return 6;
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
