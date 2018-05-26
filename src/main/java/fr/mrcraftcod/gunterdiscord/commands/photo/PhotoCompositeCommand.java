package fr.mrcraftcod.gunterdiscord.commands.photo;

import fr.mrcraftcod.gunterdiscord.commands.generic.CompositeCommand;
import net.dv8tion.jda.core.entities.ChannelType;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-05-26
 */
public class PhotoCompositeCommand extends CompositeCommand
{
	/**
	 * Constructor.
	 */
	public PhotoCompositeCommand()
	{
		super();
		addSubCommand(new GetPhotoCommand(this));
		addSubCommand(new DelPhotoCommand(this));
		addSubCommand(new AddPhotoCommand(this));
		addSubCommand(new ListPhotoCommand(this));
	}
	
	@Override
	public int getScope()
	{
		return ChannelType.TEXT.getId();
	}
	
	@Override
	public String getName()
	{
		return "Trombinoscope";
	}
	
	@Override
	public List<String> getCommand()
	{
		return List.of("photo");
	}
	
	@Override
	public String getDescription()
	{
		return "Point d'entré des fonctionnalitées du trombinoscope";
	}
	
	@Override
	public AccessLevel getAccessLevel()
	{
		return AccessLevel.ALL;
	}
}
