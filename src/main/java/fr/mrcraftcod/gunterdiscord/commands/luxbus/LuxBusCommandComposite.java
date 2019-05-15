package fr.mrcraftcod.gunterdiscord.commands.luxbus;

import fr.mrcraftcod.gunterdiscord.commands.generic.CommandComposite;
import java.util.List;

public class LuxBusCommandComposite extends CommandComposite{
	public LuxBusCommandComposite(){
		this.addSubCommand(new LuxBusGetStopCommand(this));
	}
	
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@Override
	public String getName(){
		return "Luxembourg bus";
	}
	
	@Override
	public List<String> getCommand(){
		return List.of("luxbus", "bus");
	}
	
	@Override
	public String getDescription(){
		return "Interact with the busses command";
	}
	
	@Override
	public int getScope(){
		return 0;
	}
}
