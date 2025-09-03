package fr.rakambda.rsndiscord.spring.interaction.slash.impl.moderation;

import fr.rakambda.rsndiscord.spring.interaction.slash.api.IExecutableSlashCommandGuild;
import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.forums.ForumTag;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;

@Slf4j
@Component
public class ClearThreadsCommand implements IExecutableSlashCommandGuild{
	public static final String CHANNEL_OPTION_ID = "channel";
	public static final String EXCLUDE_OPTION_ID = "exclude";
	
	@Override
	@NonNull
	public String getId(){
		return "clear-threads";
	}
	
	@Override
	@NonNull
	public String getPath(){
		return "mod/clear-threads";
	}
	
	@Override
	@NonNull
	public CompletableFuture<?> executeGuild(@NonNull SlashCommandInteraction event, @NonNull Guild guild, @NonNull Member member){
		var deferred = event.deferReply(true).submit();
		
		var targetChannel = event.getOption(CHANNEL_OPTION_ID).getAsChannel().asForumChannel();
		
		var deleteTagPredicate = Optional.ofNullable(event.getOption(EXCLUDE_OPTION_ID))
				.map(OptionMapping::getAsString)
				.map(s -> Arrays.asList(s.split(",")))
				.map(l -> (Predicate<ForumTag>) tag -> !l.contains(tag.getId()) && !l.contains(tag.getName()))
				.orElse(tag -> true);
		
		return deferred
				.thenCompose(empty -> targetChannel.retrieveArchivedPublicThreadChannels()
						.forEachAsync(thread -> {
							if(thread.getAppliedTags().stream().allMatch(deleteTagPredicate)){
								try{
									JDAWrappers.delete(thread).submit().get();
								}
								catch(InterruptedException | ExecutionException e){
									log.error("Failed waiting for thread to be deleted");
								}
							}
							return true;
						}))
				.thenCompose(empty -> JDAWrappers.edit(event, "Clear threads done").submit());
	}
}
