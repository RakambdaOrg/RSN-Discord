package fr.mrcraftcod.gunterdiscord.listeners.reply;

import fr.mrcraftcod.gunterdiscord.utils.BasicEmotes;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GenericGuildMessageEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

public class EmoteWaitingUserReply<T> extends BasicWaitingUserReply{
	private final Map<BasicEmotes, T> options;
	private final BiConsumer<GuildMessageReactionAddEvent, T> onValue;
	
	public EmoteWaitingUserReply(final Map<BasicEmotes, T> options, final GenericGuildMessageEvent event, final User author, final Message infoMessage, final BiConsumer<GuildMessageReactionAddEvent, T> onValue){
		super(event, author, infoMessage);
		this.options = options;
		this.onValue = onValue;
	}
	
	@Override
	public boolean onExecute(@Nonnull final GuildMessageReactionAddEvent event){
		if(!Objects.equals(event.getUser(), event.getJDA().getSelfUser())){
			final var replyEmote = BasicEmotes.getEmote(event.getReactionEmote().getName());
			if(Objects.nonNull(replyEmote)){
				if(this.options.containsKey(replyEmote)){
					final var value = this.options.get(replyEmote);
					this.onValue.accept(event, value);
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	protected boolean onExecute(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args){
		return false;
	}
	
	@Override
	public long getEmoteMessageId(){
		return this.getInfoMessages().stream().map(Message::getIdLong).findAny().orElse(-1L);
	}
}
