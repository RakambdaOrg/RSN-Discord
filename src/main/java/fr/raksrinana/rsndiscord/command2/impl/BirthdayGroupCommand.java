package fr.raksrinana.rsndiscord.command2.impl;

import fr.raksrinana.rsndiscord.command2.BotSlashCommand;
import fr.raksrinana.rsndiscord.command2.base.group.SubCommandsGroupCommand;
import fr.raksrinana.rsndiscord.command2.impl.birthday.AddCommand;
import fr.raksrinana.rsndiscord.command2.impl.birthday.GetCommand;
import fr.raksrinana.rsndiscord.command2.impl.birthday.ListCommand;
import fr.raksrinana.rsndiscord.command2.impl.birthday.RemoveCommand;
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
