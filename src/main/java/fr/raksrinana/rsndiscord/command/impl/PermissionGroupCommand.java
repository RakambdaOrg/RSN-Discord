package fr.raksrinana.rsndiscord.command.impl;

import fr.raksrinana.rsndiscord.command.BotSlashCommand;
import fr.raksrinana.rsndiscord.command.base.group.SubCommandsGroupGroupCommand;
import fr.raksrinana.rsndiscord.command.impl.permission.RoleSubCommandGroupCommand;
import fr.raksrinana.rsndiscord.command.impl.permission.UserSubCommandGroupCommand;
import org.jetbrains.annotations.NotNull;

@BotSlashCommand
public class PermissionGroupCommand extends SubCommandsGroupGroupCommand{
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
