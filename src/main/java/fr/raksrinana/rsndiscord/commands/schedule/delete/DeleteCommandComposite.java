package fr.raksrinana.rsndiscord.commands.schedule.delete;

import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import lombok.NonNull;
import java.util.List;

public class DeleteCommandComposite extends CommandComposite{
	public DeleteCommandComposite(Command parent){
		super(parent);
		this.addSubCommand(new ChannelCommand(this));
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Delete";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("delete");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Delete related commands";
	}
}
