package fr.raksrinana.rsndiscord.interaction.command.slash.impl;

import fr.raksrinana.rsndiscord.interaction.command.slash.api.BotSlashCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.base.group.SubCommandsGroupGroupSlashCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.impl.permission.RoleSubCommandGroupCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.impl.permission.UserSubCommandGroupCommand;
import org.jetbrains.annotations.NotNull;

@BotSlashCommand
public class PermissionGroupCommand extends SubCommandsGroupGroupSlashCommand{
	public PermissionGroupCommand(){
		addSubcommandGroup(new RoleSubCommandGroupCommand());
		addSubcommandGroup(new UserSubCommandGroupCommand());
	}
	
	@Override
	@NotNull
	public String getId(){
		return "permission";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Define guild's command permissions";
	}
}
