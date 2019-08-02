package fr.mrcraftcod.gunterdiscord.commands.photo;

import fr.mrcraftcod.gunterdiscord.commands.generic.BotCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandComposite;
import net.dv8tion.jda.api.entities.ChannelType;
import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-05-26
 */
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
	
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	
	@Nonnull
	@Override
	public String getName(){
		return "Trombinoscope";
	}
	
	@Nonnull
	
	@Override
	public List<String> getCommandStrings(){
		return List.of("photo");
	}
	
	@Nonnull
	
	@Override
	public String getDescription(){
		return "Functions of the trombinoscope";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
