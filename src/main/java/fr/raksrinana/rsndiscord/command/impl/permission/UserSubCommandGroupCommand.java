package fr.raksrinana.rsndiscord.command.impl.permission;

import fr.raksrinana.rsndiscord.command.base.group.SubCommandGroup;
import fr.raksrinana.rsndiscord.command.impl.permission.user.AllowCommand;
import fr.raksrinana.rsndiscord.command.impl.permission.user.DenyCommand;
import fr.raksrinana.rsndiscord.command.impl.permission.user.RemoveCommand;
import org.jetbrains.annotations.NotNull;

public class UserSubCommandGroupCommand extends SubCommandGroup{
	public UserSubCommandGroupCommand(){
		addSubcommand(new AllowCommand());
		addSubcommand(new DenyCommand());
		addSubcommand(new RemoveCommand());
	}
	
	@Override
	@NotNull
	public String getId(){
		return "user";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Define permissions for a user";
	}
}
