package fr.raksrinana.rsndiscord.command.impl.settings.guild.randomkick;

import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.impl.settings.helpers.SetConfigurationCommand;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.util.*;
import static fr.raksrinana.rsndiscord.permission.PermissionUtils.ALLOW;

public class KickableRolesConfigurationCommand extends SetConfigurationCommand<RoleConfiguration> {
	public KickableRolesConfigurationCommand(Command parent) {
		super(parent);
	}

	@Override
	public @NotNull IPermission getPermission() {
		return ALLOW;
	}

	@NotNull
	@Override
	protected Optional<Set<RoleConfiguration>> getConfig(@NotNull Guild guild) {
		return Optional.of(Settings.get(guild).getRandomKick().getKickableRoles());
	}

	@NotNull
	@Override
	protected RoleConfiguration extractValue(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args) throws IllegalArgumentException {
		if (event.getMessage().getMentionedRoles().isEmpty()) {
			throw new IllegalArgumentException("Please mention a role");
		}
		return new RoleConfiguration(event.getMessage().getMentionedRoles().get(0));
	}

	@Override
	protected void removeConfig(@NotNull Guild guild, @NotNull RoleConfiguration value) {
		Settings.get(guild).getRandomKick().getKickableRoles().remove(value);
	}

	@Override
	protected void createConfig(@NotNull Guild guild, @NotNull RoleConfiguration value) {
		var set = new HashSet<RoleConfiguration>();
		set.add(value);
		Settings.get(guild).getRandomKick().setKickableRoles(set);
	}

	@Override
	public void addHelp(@NotNull Guild guild, @NotNull EmbedBuilder builder) {
		super.addHelp(guild, builder);
		builder.addField("Role", "The role to add or remove", false);
	}

	@NotNull
	@Override
	public String getCommandUsage() {
		return super.getCommandUsage() + " [role]";
	}

	@NotNull
	@Override
	public String getName(@NotNull Guild guild) {
		return "Roles that can be randomly targeted for a random kick";
	}

	@NotNull
	@Override
	public List<String> getCommandStrings() {
		return List.of("kickableRoles");
	}
}
