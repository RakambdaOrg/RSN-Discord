package fr.raksrinana.rsndiscord.commands.trombinoscope;

import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import lombok.NonNull;
import java.util.List;

@BotCommand
public class TrombinoscopeCommandComposite extends CommandComposite{
	/**
	 * Constructor.
	 */
	public TrombinoscopeCommandComposite(){
		super();
		this.addSubCommand(new AddCommand(this));
		this.addSubCommand(new GetCommand(this));
		this.addSubCommand(new RemoveCommand(this));
		this.addSubCommand(new StatsCommand(this));
	}
	
	@Override
	public @NonNull AccessLevel getAccessLevel(){
		return AccessLevel.MODERATOR;
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Trombinoscope";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("trombinoscope", "trombi", "t");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Trombinoscope related commands";
	}
}
