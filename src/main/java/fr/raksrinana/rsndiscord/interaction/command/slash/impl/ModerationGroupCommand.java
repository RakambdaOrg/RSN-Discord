package fr.raksrinana.rsndiscord.interaction.command.slash.impl;

import fr.raksrinana.rsndiscord.interaction.command.slash.api.BotSlashCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.base.group.SubCommandsGroupSlashCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.impl.moderation.ClearCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.impl.moderation.ClearThreadCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import org.jetbrains.annotations.NotNull;

@BotSlashCommand
public class ModerationGroupCommand extends SubCommandsGroupSlashCommand{
	public ModerationGroupCommand(){
		addSubcommand(new ClearCommand());
		addSubcommand(new ClearThreadCommand());
	}
	
	@Override
	public @NotNull String getId(){
		return "mod";
	}
	
	@Override
	public @NotNull String getShortDescription(){
		return "Moderation commands";
	}
	
	@Override
	public DefaultMemberPermissions getDefaultPermission(){
		return DefaultMemberPermissions.enabledFor(Permission.MANAGE_CHANNEL);
	}
}
