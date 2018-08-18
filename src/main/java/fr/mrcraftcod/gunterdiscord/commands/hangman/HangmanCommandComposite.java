package fr.mrcraftcod.gunterdiscord.commands.hangman;

import fr.mrcraftcod.gunterdiscord.commands.generic.CommandComposite;
import net.dv8tion.jda.core.entities.ChannelType;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-12
 */
public class HangmanCommandComposite extends CommandComposite{
	/**
	 * Constructor.
	 */
	public HangmanCommandComposite(){
		super();
		addSubCommand(new HangmanJoinCommand(this));
		addSubCommand(new HangmanLeaveCommand(this));
		addSubCommand(new HangmanLetterCommand(this));
		addSubCommand(new HangmanSkipCommand(this));
		addSubCommand(new HangmanWordCommand(this));
	}
	
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@Override
	public String getName(){
		return "Pendu";
	}
	
	@Override
	public List<String> getCommand(){
		return List.of("pendu");
	}
	
	@Override
	public String getDescription(){
		return "Commandes liées au pendu";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
