package fr.mrcraftcod.gunterdiscord.commands.warevolves;

import fr.mrcraftcod.gunterdiscord.commands.generic.CompositeCommand;
import net.dv8tion.jda.core.entities.ChannelType;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-05-31
 */
public class WerewolvesCommandComposite extends CompositeCommand
{
	public WerewolvesCommandComposite()
	{
		super();
		addSubCommand(new WerewolvesStartCommand(this));
		addSubCommand(new WerewolvesStopCommand(this));
	}
	
	@Override
	public int getScope()
	{
		return ChannelType.TEXT.getId();
	}
	
	@Override
	public String getName()
	{
		return "Loups-garous";
	}
	
	@Override
	public List<String> getCommand()
	{
		return List.of("werewolves", "ww");
	}
	
	@Override
	public String getDescription()
	{
		return "Commande pour `Les Loups-garous de Thiercelieux`";
	}
	
	@Override
	public AccessLevel getAccessLevel()
	{
		return AccessLevel.ALL;
	}
}
