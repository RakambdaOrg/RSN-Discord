package fr.raksrinana.rsndiscord.modules.stopwatch.command;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.modules.permission.IPermission;
import fr.raksrinana.rsndiscord.modules.permission.SimplePermission;
import fr.raksrinana.rsndiscord.modules.stopwatch.reply.StopwatchReply;
import fr.raksrinana.rsndiscord.modules.stopwatch.reply.StopwatchWaitingUserReplyEventListener;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import fr.raksrinana.rsndiscord.utils.Utilities;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@BotCommand
public class StopwatchCommand extends BasicCommand {
    @NonNull
    @Override
    public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
        super.execute(event, args);
        var embed = buildEmbed(event.getGuild(), event.getAuthor(), "");
        Actions.sendEmbed(event.getChannel(), embed).thenAccept(message -> {
            Actions.addReaction(message, BasicEmotes.P.getValue());
            Actions.addReaction(message, BasicEmotes.S.getValue());
            StopwatchWaitingUserReplyEventListener.handleReply(new StopwatchReply(event, message));
        });
        return CommandResult.SUCCESS;
    }
    
    @NonNull
    public static MessageEmbed buildEmbed(@NonNull Guild guild, @NonNull User user, @NonNull String time){
        final var builder = Utilities.buildEmbed(user, Color.GREEN, translate(guild, "stopwatch.name"), null);
        builder.addField(translate(guild, "stopwatch.time"), time, false);
        builder.addField(BasicEmotes.P.getValue(), translate(guild, "stopwatch.pause"), true);
        builder.addField(BasicEmotes.R.getValue(), translate(guild, "stopwatch.resume"), true);
        builder.addField(BasicEmotes.S.getValue(), translate(guild, "stopwatch.stop"), true);
        return builder.build();
    }
    
    @Override
    public @NonNull IPermission getPermission(){
        return new SimplePermission("command.stopwatch", false);
    }

    @NonNull
    @Override
    public String getName(@NonNull Guild guild) {
        return translate(guild, "command.stopwatch.name");
    }

    @NonNull
    @Override
    public List<String> getCommandStrings() {
        return List.of("stopwatch");
    }

    @NonNull
    @Override
    public String getDescription(@NonNull Guild guild) {
        return translate(guild, "command.stopwatch.description");
    }
}
