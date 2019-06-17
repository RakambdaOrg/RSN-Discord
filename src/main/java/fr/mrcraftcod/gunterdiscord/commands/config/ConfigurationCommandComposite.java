package fr.mrcraftcod.gunterdiscord.commands.config;

import fr.mrcraftcod.gunterdiscord.commands.generic.CommandComposite;
import net.dv8tion.jda.api.entities.ChannelType;
import java.util.List;
import static fr.mrcraftcod.gunterdiscord.commands.config.ConfigurationCommand.ChangeConfigType.*;
import static fr.mrcraftcod.gunterdiscord.commands.generic.Command.AccessLevel.ADMIN;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 13/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-13
 */
public class ConfigurationCommandComposite extends CommandComposite{
	/**
	 * Constructor.
	 */
	public ConfigurationCommandComposite(){
		super();
		addSubCommand(new ConfigurationCommand(this, ADD, List.of("add", "a")));
		addSubCommand(new ConfigurationCommand(this, REMOVE, List.of("remove", "r")));
		addSubCommand(new ConfigurationCommand(this, SET, List.of("set", "s")));
		addSubCommand(new ConfigurationCommand(this, SHOW, List.of("show", "l")));
		addSubCommand(new ListConfigurationCommand(this));
	}
	
	@Override
	public AccessLevel getAccessLevel(){
		return ADMIN;
	}
	
	@Override
	public String getName(){
		return "Config";
	}
	
	@Override
	public List<String> getCommandStrings(){
		return List.of("config");
	}
	
	@Override
	public String getDescription(){
		return "Interact with the bot's configuration";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
