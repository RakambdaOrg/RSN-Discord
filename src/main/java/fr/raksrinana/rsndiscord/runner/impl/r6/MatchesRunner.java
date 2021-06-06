package fr.raksrinana.rsndiscord.runner.impl.r6;

import fr.raksrinana.rsndiscord.api.pandascore.PandaScoreApi;
import fr.raksrinana.rsndiscord.api.pandascore.data.match.RainbowSixMatch;
import fr.raksrinana.rsndiscord.api.pandascore.request.GetR6MatchById;
import fr.raksrinana.rsndiscord.api.pandascore.request.GetRunningR6Matches;
import fr.raksrinana.rsndiscord.runner.api.IScheduledRunner;
import fr.raksrinana.rsndiscord.runner.api.ScheduledRunner;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import org.jetbrains.annotations.NotNull;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import static java.util.concurrent.TimeUnit.MINUTES;

@ScheduledRunner
public class MatchesRunner implements IScheduledRunner{
	private final Map<Integer, Map<Long, Long>> matchesInProgress;
	
	public MatchesRunner(){
		matchesInProgress = new HashMap<>();
	}
	
	@Override
	public void executeGlobal(@NotNull JDA jda){
		var request = new GetRunningR6Matches();
		PandaScoreApi.executeGetRequest(request).ifPresent(matches -> {
			processOngoingMatches(jda, matches);
			processNewMatches(jda, matches);
			processEndedMatches(jda, matches);
		});
	}
	
	@Override
	public void executeGuild(@NotNull Guild guild){
	}
	
	private void processNewMatches(@NotNull JDA jda, @NotNull List<RainbowSixMatch> matches){
		matches.stream()
				.filter(match -> !matchesInProgress.containsKey(match.getId()))
				.forEach(match -> {
					var embed = createEmbed(jda.getSelfUser(), match);
					sendToChannels(jda, channel -> newMatch(embed, channel)
							.thenAccept(message -> setMessageId(match.getId(), channel.getIdLong(), message.getIdLong())));
				});
	}
	
	private void processOngoingMatches(@NotNull JDA jda, @NotNull List<RainbowSixMatch> matches){
		matches.stream()
				.filter(match -> matchesInProgress.containsKey(match.getId()))
				.forEach(match -> {
					var embed = createEmbed(jda.getSelfUser(), match);
					sendToChannels(jda, channel -> updateMatch(embed, channel, getMessageId(match.getId(), channel.getIdLong()).orElseThrow())
							.thenAccept(message -> setMessageId(match.getId(), channel.getIdLong(), message.getIdLong())));
				});
	}
	
	private void processEndedMatches(@NotNull JDA jda, @NotNull List<RainbowSixMatch> matches){
		matchesInProgress.keySet().stream()
				.filter(id -> matches.stream().noneMatch(m -> Objects.equals(m.getId(), id)))
				.flatMap(slug -> {
					var request = new GetR6MatchById(slug);
					return PandaScoreApi.executeGetRequest(request)
							.stream()
							.flatMap(List::stream);
				})
				.forEach(match -> {
					var embed = createEmbed(jda.getSelfUser(), match);
					sendToChannels(jda, channel -> updateMatch(embed, channel, getMessageId(match.getId(), channel.getIdLong()).orElseThrow())
							.thenAccept(message -> removeIdForMatch(match.getId(), channel.getIdLong())));
				});
	}
	
	private Optional<Long> getMessageId(int matchId, long channelId){
		return Optional.ofNullable(matchesInProgress.get(matchId))
				.map(map -> map.get(channelId));
	}
	
	private void setMessageId(int matchId, long channelId, long messageId){
		matchesInProgress.computeIfAbsent(matchId, key -> new HashMap<>())
				.put(channelId, messageId);
	}
	
	private void removeIdForMatch(int matchId, long channelId){
		Optional.ofNullable(matchesInProgress.get(matchId))
				.ifPresent(map -> {
					map.remove(channelId);
					if(map.isEmpty()){
						matchesInProgress.remove(matchId);
					}
				});
	}
	
	@NotNull
	private CompletionStage<Message> newMatch(@NotNull MessageEmbed embed, @NotNull TextChannel channel){
		return JDAWrappers.message(channel, embed).submit();
	}
	
	@NotNull
	private CompletableFuture<Message> updateMatch(@NotNull MessageEmbed embed, @NotNull TextChannel channel, @NotNull Long originalMessageId){
		return channel.retrieveMessageById(originalMessageId).submit()
				.thenCompose(originalMessage -> JDAWrappers.edit(originalMessage, embed).submit());
	}
	
	@NotNull
	private MessageEmbed createEmbed(@NotNull SelfUser selfUser, @NotNull RainbowSixMatch match){
		var builder = new EmbedBuilder().setAuthor(selfUser.getName(), null, selfUser.getAvatarUrl());
		match.fillEmbed(builder);
		return builder.build();
	}
	
	private void sendToChannels(@NotNull JDA jda, @NotNull Consumer<TextChannel> channelConsumer){
		jda.getGuilds().forEach(guild -> Settings.get(guild)
				.getRainbowSixConfiguration()
				.getMatchNotificationChannel()
				.flatMap(ChannelConfiguration::getChannel)
				.ifPresent(channelConsumer));
	}
	
	@Override
	public long getDelay(){
		return 0;
	}
	
	@NotNull
	@Override
	public String getName(){
		return "R6 ProLeague matches";
	}
	
	@Override
	public long getPeriod(){
		return 3;
	}
	
	@NotNull
	@Override
	public TimeUnit getPeriodUnit(){
		return MINUTES;
	}
}
