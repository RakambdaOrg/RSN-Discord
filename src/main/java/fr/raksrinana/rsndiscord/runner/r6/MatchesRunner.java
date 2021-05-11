package fr.raksrinana.rsndiscord.runner.r6;

import fr.raksrinana.rsndiscord.api.pandascore.PandaScoreApi;
import fr.raksrinana.rsndiscord.api.pandascore.data.R6Match;
import fr.raksrinana.rsndiscord.api.pandascore.request.GetR6MatchBySlug;
import fr.raksrinana.rsndiscord.api.pandascore.request.GetRunningR6Matches;
import fr.raksrinana.rsndiscord.runner.IScheduledRunner;
import fr.raksrinana.rsndiscord.runner.ScheduledRunner;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.util.concurrent.TimeUnit.MINUTES;

@ScheduledRunner
public class MatchesRunner implements IScheduledRunner {
    private final JDA jda;
    private final Map<String, Map<Long, Long>> matchesInProgress;

    public MatchesRunner(@NotNull JDA jda) {
        this.jda = jda;
        matchesInProgress = new HashMap<>();
    }

    @Override
    public void execute() {
        var request = new GetRunningR6Matches();
        PandaScoreApi.executeGetRequest(request).ifPresent(matches -> {
            processOngoingMatches(matches);
            processNewMatches(matches);
            processEndedMatches(matches);
        });
    }

    private void processNewMatches(List<R6Match> matches) {
        matches.stream()
                .filter(match -> !matchesInProgress.containsKey(match.getSlug()))
                .forEach(match -> {
                    var embed = createEmbed(match);
                    sendToChannels(channel -> newMatch(embed, channel)
                            .thenAccept(message -> setMessageId(match.getSlug(), channel.getIdLong(), message.getIdLong())));
                });
    }

    private void processOngoingMatches(List<R6Match> matches) {
        matches.stream()
                .filter(match -> matchesInProgress.containsKey(match.getSlug()))
                .forEach(match -> {
                    var embed = createEmbed(match);
                    sendToChannels(channel -> updateMatch(embed, channel, getMessageId(match.getSlug(), channel.getIdLong()).orElseThrow())
                            .thenAccept(message -> setMessageId(match.getSlug(), channel.getIdLong(), message.getIdLong())));
                });
    }

    private void processEndedMatches(List<R6Match> matches) {
        matchesInProgress.keySet().stream()
                .filter(slug -> matches.stream().noneMatch(m -> Objects.equals(m.getSlug(), slug)))
                .flatMap(slug -> {
                    var request = new GetR6MatchBySlug(slug);
                    return PandaScoreApi.executeGetRequest(request)
                            .stream()
                            .flatMap(List::stream);
                })
                .forEach(match -> {
                    var embed = createEmbed(match);
                    sendToChannels(channel -> updateMatch(embed, channel, getMessageId(match.getSlug(), channel.getIdLong()).orElseThrow())
                            .thenAccept(message -> removeIdForMatch(match.getSlug(), channel.getIdLong())));
                });
    }

    private Optional<Long> getMessageId(String slug, long channelId) {
        return Optional.ofNullable(matchesInProgress.get(slug))
                .map(map -> map.get(channelId));
    }

    private void setMessageId(String slug, long channelId, long messageId) {
        matchesInProgress.computeIfAbsent(slug, key -> new HashMap<>())
                .put(channelId, messageId);
    }

    private void removeIdForMatch(String slug, long channelId) {
        Optional.ofNullable(matchesInProgress.get(slug))
                .ifPresent(map -> {
                    map.remove(channelId);
                    if (map.isEmpty())
                        matchesInProgress.remove(slug);
                });
    }

    private CompletionStage<Message> newMatch(MessageEmbed embed, TextChannel channel) {
        return JDAWrappers.message(channel, embed).submit();
    }

    private CompletableFuture<Message> updateMatch(MessageEmbed embed, TextChannel channel, Long originalMessageId) {
        return channel.retrieveMessageById(originalMessageId).submit()
                .thenCompose(originalMessage -> JDAWrappers.edit(originalMessage, embed).submit());
    }

    private MessageEmbed createEmbed(R6Match match) {
        var selfUser = jda.getSelfUser();

        return new EmbedBuilder().setAuthor(selfUser.getName(), null, selfUser.getAvatarUrl())
                .setColor(Color.GREEN)
                .setTitle(match.getName())
                .setTimestamp(match.getModifyDate())
                .build();
    }

    private void sendToChannels(Consumer<TextChannel> channelConsumer) {
        jda.getGuilds().forEach(guild -> Settings.get(guild)
                .getRainbowSixConfiguration()
                .getMatchNotificationChannel()
                .flatMap(ChannelConfiguration::getChannel)
                .ifPresent(channelConsumer));
    }

    @Override
    public long getDelay() {
        return 2;
    }

    @NotNull
    @Override
    public String getName() {
        return "R6 ProLeague matches";
    }

    @Override
    public long getPeriod() {
        return 5;
    }

    @NotNull
    @Override
    public TimeUnit getPeriodUnit() {
        return MINUTES;
    }
}
