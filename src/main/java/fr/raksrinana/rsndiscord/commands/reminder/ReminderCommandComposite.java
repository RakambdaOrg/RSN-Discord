package fr.raksrinana.rsndiscord.commands.reminder;

import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import lombok.NonNull;
import java.util.List;

@BotCommand
public class ReminderCommandComposite extends CommandComposite{
	/**
	 * Constructor.
	 */
	public ReminderCommandComposite(){
		super();
		this.addSubCommand(new DelayReminderCommand(this));
		this.addSubCommand(new DateReminderCommand(this));
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Reminder";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("reminder");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Add reminders to be notified later";
	}
}
