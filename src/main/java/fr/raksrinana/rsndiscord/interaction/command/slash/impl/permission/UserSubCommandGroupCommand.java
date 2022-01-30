package fr.raksrinana.rsndiscord.interaction.command.slash.impl.permission;

import fr.raksrinana.rsndiscord.interaction.command.slash.base.group.SubGroupSlashCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.impl.permission.user.AllowCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.impl.permission.user.DenyCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.impl.permission.user.RemoveCommand;
import org.jetbrains.annotations.NotNull;

public class UserSubCommandGroupCommand extends SubGroupSlashCommand{
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
