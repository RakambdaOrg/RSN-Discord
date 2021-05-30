package fr.raksrinana.rsndiscord.command2.impl.permission;

import fr.raksrinana.rsndiscord.command2.base.group.SubCommandGroup;
import fr.raksrinana.rsndiscord.command2.impl.permission.role.AllowCommand;
import fr.raksrinana.rsndiscord.command2.impl.permission.role.DenyCommand;
import fr.raksrinana.rsndiscord.command2.impl.permission.role.RemoveCommand;
import org.jetbrains.annotations.NotNull;

public class RoleSubCommandGroupCommand extends SubCommandGroup{
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
