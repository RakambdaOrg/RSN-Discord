package fr.raksrinana.rsndiscord.commands.amazon;

import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import lombok.NonNull;
import java.util.List;

@BotCommand
public class AmazonCommandComposite extends CommandComposite{
	/**
	 * Constructor.
	 */
	public AmazonCommandComposite(){
		super();
		this.addSubCommand(new AddProductCommand(this));
		this.addSubCommand(new UpdateTrackedProductsCommand(this));
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Amazon";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("amazon");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Amazon related commands";
	}
}
