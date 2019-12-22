package fr.raksrinana.rsndiscord.commands.trakt;

import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import lombok.NonNull;
import java.util.List;

@BotCommand
public class TraktCommandComposite extends CommandComposite{
	/**
	 * Constructor.
	 */
	public TraktCommandComposite(){
		super();
		this.addSubCommand(new RegisterCommand(this));
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Trakt";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("trakt");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Trakt related commands";
	}
}
