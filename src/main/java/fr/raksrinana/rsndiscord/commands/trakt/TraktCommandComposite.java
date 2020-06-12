package fr.raksrinana.rsndiscord.commands.trakt;

import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;

import java.util.List;

import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@BotCommand
public class TraktCommandComposite extends CommandComposite {
    /**
     * Constructor.
     */
    public TraktCommandComposite() {
        super();
        this.addSubCommand(new RegisterCommand(this));
        this.addSubCommand(new HistoryCommand(this));
    }

    @NonNull
    @Override
    public String getName(@NonNull Guild guild) {
        return translate(guild, "command.trakt.name");
    }

    @NonNull
    @Override
    public List<String> getCommandStrings() {
        return List.of("trakt");
    }

    @NonNull
    @Override
    public String getDescription(@NonNull Guild guild) {
        return translate(guild, "command.trakt.description");
    }
}
