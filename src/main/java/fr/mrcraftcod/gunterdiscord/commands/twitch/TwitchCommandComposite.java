package fr.mrcraftcod.gunterdiscord.commands.twitch;

import fr.mrcraftcod.gunterdiscord.commands.generic.CommandComposite;
import net.dv8tion.jda.api.entities.ChannelType;
import javax.annotation.Nonnull;
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
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ADMIN;
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Twitch";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("twitch", "tw");
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return "Handles twitch interactions";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
