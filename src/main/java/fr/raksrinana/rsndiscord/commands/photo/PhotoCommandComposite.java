package fr.raksrinana.rsndiscord.commands.photo;

import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import lombok.NonNull;
import java.util.List;

@BotCommand
public class PhotoCommandComposite extends CommandComposite{
	/**
	 * Constructor.
	 */
	public PhotoCommandComposite(){
		super();
		this.addSubCommand(new PhotoGetCommand(this));
		this.addSubCommand(new PhotoDeleteCommand(this));
		this.addSubCommand(new PhotoAddCommand(this));
		this.addSubCommand(new PhotoListCommand(this));
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Trombinoscope";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("photo");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Functions of the trombinoscope";
	}
}
