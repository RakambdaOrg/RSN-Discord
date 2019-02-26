package fr.mrcraftcod.gunterdiscord.commands.twitch;

import fr.mrcraftcod.gunterdiscord.commands.generic.CommandComposite;
import net.dv8tion.jda.api.entities.ChannelType;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-08-18
 */
public class TwitchCommandComposite extends CommandComposite{
	/**
	 * Constructor.
	 */
	public TwitchCommandComposite(){
		addSubCommand(new ConnectCommand(this));
		addSubCommand(new DisconnectCommand(this));
		addSubCommand(new QuitCommand(this));
	}
	
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ADMIN;
	}
	
	@Override
	public String getName(){
		return "Twitch";
	}
	
	@Override
	public List<String> getCommand(){
		return List.of("twitch", "tw");
	}
	
	@Override
	public String getDescription(){
		return "Handles twitch interactions";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
