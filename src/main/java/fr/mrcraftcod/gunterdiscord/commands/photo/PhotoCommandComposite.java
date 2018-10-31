package fr.mrcraftcod.gunterdiscord.commands.photo;

import fr.mrcraftcod.gunterdiscord.commands.generic.CommandComposite;
import net.dv8tion.jda.core.entities.ChannelType;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-05-26
 */
public class PhotoCommandComposite extends CommandComposite{
	/**
	 * Constructor.
	 */
	public PhotoCommandComposite(){
		super();
		addSubCommand(new PhotoGetCommand(this));
		addSubCommand(new PhotoDeleteCommand(this));
		addSubCommand(new PhotoAddCommand(this));
		addSubCommand(new PhotoListCommand(this));
	}
	
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@Override
	public String getName(){
		return "Trombinoscope";
	}
	
	@Override
	public List<String> getCommand(){
		return List.of("photo");
	}
	
	@Override
	public String getDescription(){
		return "Functions of the trombinoscope";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
