package fr.raksrinana.rsndiscord.command.impl;

import fr.raksrinana.rsndiscord.command.BotSlashCommand;
import fr.raksrinana.rsndiscord.command.base.group.SubCommandsGroupCommand;
import fr.raksrinana.rsndiscord.command.impl.birthday.AddCommand;
import fr.raksrinana.rsndiscord.command.impl.birthday.GetCommand;
import fr.raksrinana.rsndiscord.command.impl.birthday.ListCommand;
import fr.raksrinana.rsndiscord.command.impl.birthday.RemoveCommand;
import org.jetbrains.annotations.NotNull;

@BotSlashCommand
public class BirthdayGroupCommand extends SubCommandsGroupCommand{
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
	public boolean getDefaultPermission(){
		return false;
	}
}
