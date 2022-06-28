package fr.raksrinana.rsndiscord.interaction.command.slash.impl;

import fr.raksrinana.rsndiscord.interaction.command.slash.api.BotSlashCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.base.group.SubCommandsGroupSlashCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.impl.birthday.AddCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.impl.birthday.GetCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.impl.birthday.ListCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.impl.birthday.RemoveCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import org.jetbrains.annotations.NotNull;

@BotSlashCommand
public class BirthdayGroupCommand extends SubCommandsGroupSlashCommand{
	public BirthdayGroupCommand(){
		addSubcommand(new AddCommand());
		addSubcommand(new GetCommand());
		addSubcommand(new ListCommand());
		addSubcommand(new RemoveCommand());
	}
	
	@Override
	@NotNull
	public String getId(){
		return "birthday";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Birthday of the users";
	}
	
	@Override
	public DefaultMemberPermissions getDefaultPermission(){
		return DefaultMemberPermissions.enabledFor(Permission.KICK_MEMBERS);
	}
}
