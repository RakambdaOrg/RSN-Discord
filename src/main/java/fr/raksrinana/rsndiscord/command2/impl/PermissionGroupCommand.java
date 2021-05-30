package fr.raksrinana.rsndiscord.command2.impl;

import fr.raksrinana.rsndiscord.command2.BotSlashCommand;
import fr.raksrinana.rsndiscord.command2.base.group.SubCommandsGroupGroupCommand;
import fr.raksrinana.rsndiscord.command2.impl.permission.RoleSubCommandGroupCommand;
import org.jetbrains.annotations.NotNull;

@BotSlashCommand
public class PermissionGroupCommand extends SubCommandsGroupGroupCommand{
	public PermissionGroupCommand(){
		addSubcommandGroup(new RoleSubCommandGroupCommand());
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
