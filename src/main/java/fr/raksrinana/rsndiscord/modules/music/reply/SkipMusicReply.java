package fr.raksrinana.rsndiscord.modules.music.reply;

import fr.raksrinana.rsndiscord.modules.music.RSNAudioManager;
import fr.raksrinana.rsndiscord.reply.BasicWaitingUserReply;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

public class SkipMusicReply extends BasicWaitingUserReply{
	private int votesRequired;
	
	public SkipMusicReply(final GuildMessageReceivedEvent event, final Message message, int votesRequired){
		super(event, event.getAuthor(), event.getChannel(), 20, TimeUnit.SECONDS, message);
		this.votesRequired = votesRequired;
	}
	
	@Override
	protected boolean onExecute(@NonNull final GuildMessageReactionAddEvent event){
		if(!Objects.equals(event.getUser(), event.getJDA().getSelfUser())){
			final var replyEmote = BasicEmotes.getEmote(event.getReactionEmote().getName());
			if(Objects.nonNull(replyEmote)){
				if(replyEmote == BasicEmotes.CHECK_OK){
					if(count(event)){
						RSNAudioManager.skip(event.getGuild());
						Actions.sendMessage(event.getChannel(), translate(event.getGuild(), "music.skipped", "@everyone"), null, false, action -> action.allowedMentions(List.of()));
						return true;
					}
					return false;
				}
			}
		}
		return false;
	}
	
	private boolean count(GuildMessageReactionAddEvent event){
		var count = event.retrieveMessage().complete()
				.getReactions().stream()
				.filter(r -> BasicEmotes.getEmote(r.getReactionEmote().getName()) == BasicEmotes.CHECK_OK)
				.mapToInt(MessageReaction::getCount)
				.sum();
		return count >= votesRequired;
	}
	
	@Override
	public boolean onExpire(){
		stop();
		return true;
	}
	
	private void stop(){
		var channel = this.getWaitChannel();
		Actions.sendMessage(channel, translate(channel.getGuild(), "music.skip.timeout"), null);
	}
	
	@Override
	public boolean handleEvent(final GuildMessageReactionAddEvent event){
		return Objects.equals(this.getWaitChannel(), event.getChannel()) && Objects.equals(this.getEmoteMessageId(), event.getMessageIdLong());
	}
	
	@Override
	protected boolean onExecute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		return false;
	}
}
