package fr.raksrinana.rsndiscord.reply;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fr.raksrinana.rsndiscord.music.RSNAudioManager;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import static fr.raksrinana.rsndiscord.utils.BasicEmotes.CHECK_OK;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.util.concurrent.TimeUnit.SECONDS;

@Log4j2
public class SkipMusicReply extends BasicWaitingUserReply{
	private final int votesRequired;
	private final AudioTrack audioTrack;
	
	public SkipMusicReply(SlashCommandInteraction event, Message message, int votesRequired, AudioTrack audioTrack){
		super(event.getUser(), event.getTextChannel(), 20, SECONDS, message);
		this.votesRequired = votesRequired;
		this.audioTrack = audioTrack;
	}
	
	@Override
	protected boolean onExecute(@NotNull MessageReactionAddEvent event){
		if(Objects.equals(event.getUser(), event.getJDA().getSelfUser())){
			return false;
		}
		
		var guild = event.getGuild();
		var replyEmote = BasicEmotes.getEmote(event.getReactionEmote().getName());
		
		if(replyEmote != CHECK_OK){
			return false;
		}
		
		try{
			if(count(event)){
				log.info("Vote successful, skipping");
				if(isSameTrack(event)){
					RSNAudioManager.skip(guild);
					JDAWrappers.message(event, translate(guild, "music.skipped", "@everyone"))
							.allowedMentions(List.of())
							.submit();
				}
				else{
					log.info("Music isn't the same anymore, didn't skip");
				}
				return true;
			}
		}
		catch(InterruptedException | ExecutionException | TimeoutException e){
			throw new RuntimeException("Failed to process vote", e);
		}
		return false;
	}
	
	private boolean count(@NotNull MessageReactionAddEvent event) throws InterruptedException, ExecutionException, TimeoutException{
		var count = event.retrieveMessage().submit()
				.thenApply(message -> message.getReactions().stream()
						.filter(r -> BasicEmotes.getEmote(r.getReactionEmote().getName()) == CHECK_OK)
						.mapToInt(MessageReaction::getCount)
						.sum())
				.get(30, SECONDS);
		log.debug("{}/{} votes to skip current music", count, votesRequired);
		return count >= votesRequired;
	}
	
	@NotNull
	private Boolean isSameTrack(@NotNull MessageReactionAddEvent event){
		return RSNAudioManager.currentTrack(event.getGuild())
				.map(track -> Objects.equals(track, audioTrack))
				.orElse(false);
	}
	
	@Override
	public boolean onExpire(){
		stop();
		return true;
	}
	
	@Override
	public boolean handleEvent(@NotNull MessageReactionAddEvent event){
		return Objects.equals(getWaitChannel(), event.getChannel()) && Objects.equals(getEmoteMessageId(), event.getMessageIdLong());
	}
	
	@Override
	protected boolean onExecute(@NotNull MessageReceivedEvent event, @NotNull LinkedList<String> args){
		return false;
	}
	
	private void stop(){
		var channel = getWaitChannel();
		var guild = channel.getGuild();
		
		log.info("Vote not successful, music not skipped");
		
		JDAWrappers.message(channel, translate(guild, "music.skip.timeout")).submitAndDelete(5);
	}
}
