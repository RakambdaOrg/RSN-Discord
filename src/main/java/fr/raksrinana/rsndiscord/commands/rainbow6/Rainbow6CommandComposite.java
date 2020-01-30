package fr.raksrinana.rsndiscord.commands.rainbow6;

import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import lombok.NonNull;
import java.util.List;

@BotCommand
public class Rainbow6CommandComposite extends CommandComposite{
	public Rainbow6CommandComposite(){
		this.addSubCommand(new Rainbow6FetchMatchesCommand(this));
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Rainbow 6";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("rainbow6", "r6");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Rainbow 6 related commands";
	}
}
