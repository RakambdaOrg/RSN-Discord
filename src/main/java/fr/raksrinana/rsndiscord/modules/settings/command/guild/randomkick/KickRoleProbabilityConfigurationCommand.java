package fr.raksrinana.rsndiscord.modules.settings.command.guild.randomkick;

import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.modules.permission.IPermission;
import fr.raksrinana.rsndiscord.modules.settings.Settings;
import fr.raksrinana.rsndiscord.modules.settings.command.helpers.ValueConfigurationCommand;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static fr.raksrinana.rsndiscord.modules.permission.PermissionUtils.ALLOW;

public class KickRoleProbabilityConfigurationCommand extends ValueConfigurationCommand<Double> {
    public KickRoleProbabilityConfigurationCommand(final Command parent) {
        super(parent);
    }

    @Override
    public @NonNull IPermission getPermission() {
        return ALLOW;
    }

    @Override
    protected Double extractValue(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args) {
        if (args.isEmpty()) {
            throw new IllegalArgumentException("Please mention the probability");
        }
        try {
            return Double.parseDouble(args.pop());
        } catch (final NumberFormatException e) {
            throw new IllegalArgumentException("Please mention the probability");
        }
    }

    @Override
    protected void setConfig(@NonNull final Guild guild, @NonNull final Double value) {
        Settings.get(guild).getRandomKick().setKickRoleProbability(Math.min(1, Math.max(0, value)));
    }

    @Override
    protected void removeConfig(@NonNull final Guild guild) {
    }

    @Override
    protected String getValueName() {
        return "Kick role probability";
    }

    @NonNull
    @Override
    protected Optional<Double> getConfig(final Guild guild) {
        return Optional.of(Settings.get(guild).getRandomKick().getKickRoleProbability());
    }

    @NonNull
    @Override
    public String getName(@NonNull Guild guild) {
        return "Kick role probability";
    }

    @NonNull
    @Override
    public List<String> getCommandStrings() {
        return List.of("kickRoleProbability");
    }
}
