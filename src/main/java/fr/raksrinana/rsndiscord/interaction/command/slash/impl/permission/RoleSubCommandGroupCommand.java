package fr.raksrinana.rsndiscord.interaction.command.slash.impl.permission;

import fr.raksrinana.rsndiscord.interaction.command.slash.base.group.SubGroupSlashCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.impl.permission.role.AllowCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.impl.permission.role.DenyCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.impl.permission.role.RemoveCommand;
import org.jetbrains.annotations.NotNull;

public class RoleSubCommandGroupCommand extends SubGroupSlashCommand{
	public RoleSubCommandGroupCommand(){
		addSubcommand(new AllowCommand());
		addSubcommand(new DenyCommand());
		addSubcommand(new RemoveCommand());
	}
	
	@Override
	@NotNull
	public String getId(){
		return "role";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Define permissions for a role";
	}
}
