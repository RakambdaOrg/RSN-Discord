package fr.raksrinana.rsndiscord.commands.config.guild;

import fr.raksrinana.rsndiscord.commands.config.guild.warns.DoubleWarnConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.config.guild.warns.MegaWarnConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.config.guild.warns.SimpleWarnConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import lombok.NonNull;
import java.util.List;

public class WarnsConfigurationCommandComposite extends CommandComposite{
	public WarnsConfigurationCommandComposite(final Command parent){
		super(parent);
		this.addSubCommand(new SimpleWarnConfigurationCommand(this));
		this.addSubCommand(new DoubleWarnConfigurationCommand(this));
		this.addSubCommand(new MegaWarnConfigurationCommand(this));
	}
	
	@NonNull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ADMIN;
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Warns";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("warns");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Warns configurations";
	}
}
