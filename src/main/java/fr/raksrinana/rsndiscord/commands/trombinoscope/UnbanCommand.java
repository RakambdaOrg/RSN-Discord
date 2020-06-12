package fr.raksrinana.rsndiscord.commands.trombinoscope;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.Actions;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.LinkedList;
import java.util.List;

import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

class UnbanCommand extends BasicCommand {
    /**
     * Constructor.
     *
     * @param parent The parent command.
     */
    UnbanCommand(final Command parent) {
        super(parent);
    }

    @Override
    public void addHelp(@NonNull Guild guild, @NonNull EmbedBuilder builder) {
        super.addHelp(guild, builder);
        builder.addField("user", translate(guild, "command.trombinoscope.unban.help.user"), true);
    }

    @NonNull
    @Override
    public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args) {
        super.execute(event, args);
        if (event.getMessage().getMentionedUsers().isEmpty()) {
            return CommandResult.BAD_ARGUMENTS;
        }
        var trombinoscope = Settings.get(event.getGuild()).getTrombinoscope();
        var target = event.getMessage().getMentionedUsers().get(0);
        trombinoscope.unbanUser(target);
        Actions.reply(event, translate(event.getGuild(), "trombinoscope.unbanned", target.getAsMention()), null);
        return CommandResult.SUCCESS;
    }

    @Override
    public @NonNull String getCommandUsage() {
        return super.getCommandUsage() + " <user>";
    }

    @Override
    public @NonNull AccessLevel getAccessLevel() {
        return AccessLevel.MODERATOR;
    }

    @NonNull
    @Override
    public String getName(@NonNull Guild guild) {
        return translate(guild, "command.trombinoscope.unban.name");
    }

    @NonNull
    @Override
    public List<String> getCommandStrings() {
        return List.of("unban", "ub");
    }

    @NonNull
    @Override
    public String getDescription(@NonNull Guild guild) {
        return translate(guild, "command.trombinoscope.unban.description");
    }
}
