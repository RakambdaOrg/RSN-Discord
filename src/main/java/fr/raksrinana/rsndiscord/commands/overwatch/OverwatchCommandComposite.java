package fr.raksrinana.rsndiscord.commands.overwatch;

import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import lombok.NonNull;
import java.util.List;

@BotCommand
public class OverwatchCommandComposite extends CommandComposite{
	public OverwatchCommandComposite(){
		this.addSubCommand(new OverwatchGetWeekMatchesCommand(this));
		this.addSubCommand(new OverwatchGetMatchCommand(this));
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Overwatch";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("overwatch", "ow");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Overwatch related commands";
	}
}
