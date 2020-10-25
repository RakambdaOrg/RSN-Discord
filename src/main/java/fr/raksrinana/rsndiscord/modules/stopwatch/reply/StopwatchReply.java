package fr.raksrinana.rsndiscord.modules.stopwatch.reply;

import fr.raksrinana.rsndiscord.modules.stopwatch.command.StopwatchCommand;
import fr.raksrinana.rsndiscord.reply.BasicWaitingUserReply;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import fr.raksrinana.rsndiscord.utils.Utilities;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import java.io.IOException;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

public class StopwatchReply extends BasicWaitingUserReply {
    private final ScheduledExecutorService executor;
    private boolean counting = true;
    private ZonedDateTime lastStart = ZonedDateTime.now();
    private Duration totalTime = Duration.ZERO;

    public StopwatchReply(final GuildMessageReceivedEvent event, final Message message) {
        super(event, event.getAuthor(), event.getChannel(), 1, TimeUnit.DAYS, message);
        this.executor = Executors.newSingleThreadScheduledExecutor();
        this.executor.scheduleAtFixedRate(this::updateTimer, 5, 10, TimeUnit.SECONDS);
    }

    private void updateTimer() {
        final var newTotalTime = this.totalTime.plus(this.counting ? Duration.between(this.lastStart, ZonedDateTime.now()) : Duration.ZERO);
        if (!Objects.equals(newTotalTime, this.totalTime)) {
            var embed = StopwatchCommand.buildEmbed(this.getWaitChannel().getGuild(), this.getWaitUser(), Utilities.durationToString(newTotalTime));
            this.getInfoMessages().stream().findFirst().ifPresent(m -> Actions.editMessage(m, embed));
        }
    }

    @Override
    public void close() throws IOException {
        super.close();
        this.executor.shutdownNow();
    }

    @Override
    protected boolean onExecute(@NonNull final GuildMessageReactionAddEvent event) {
        if (!Objects.equals(event.getUser(), event.getJDA().getSelfUser())) {
            final var replyEmote = BasicEmotes.getEmote(event.getReactionEmote().getName());
            if (Objects.nonNull(replyEmote)) {
                if (replyEmote == BasicEmotes.S) {
                    stop();
                    return true;
                }
                if (this.counting) {
                    if (replyEmote == BasicEmotes.P) {
                        this.counting = false;
                        this.totalTime = this.totalTime.plus(Duration.between(this.lastStart, ZonedDateTime.now()));
                    }
                } else {
                    if (replyEmote == BasicEmotes.R) {
                        this.counting = true;
                        this.lastStart = ZonedDateTime.now();
                    }
                }
                final var message = this.getInfoMessages().stream().findFirst();
                message.ifPresent(m -> {
                    m.clearReactions().queue();
                    if (counting) {
                        Actions.addReaction(m, BasicEmotes.P.getValue());
                    } else {
                        Actions.addReaction(m, BasicEmotes.R.getValue());
                    }
                    Actions.addReaction(m, BasicEmotes.S.getValue());
                });
            }
        }
        return false;
    }

    @Override
    public boolean onExpire() {
        stop();
        return true;
    }

    private void stop() {
        this.counting = false;
        this.totalTime = this.totalTime.plus(Duration.between(this.lastStart, ZonedDateTime.now()));
        this.executor.shutdown();
        var channel = this.getWaitChannel();
        Actions.sendMessage(channel, translate(channel.getGuild(), "stopwatch.total-time", Utilities.durationToString(this.totalTime)), null);
    }

    @Override
    public boolean handleEvent(final GuildMessageReactionAddEvent event) {
        return Objects.equals(this.getWaitChannel(), event.getChannel()) && Objects.equals(this.getEmoteMessageId(), event.getMessageIdLong());
    }

    @Override
    protected boolean onExecute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args) {
        return false;
    }
}
