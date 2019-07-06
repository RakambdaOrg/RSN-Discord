package fr.mrcraftcod.gunterdiscord.commands.newconfig.guild;

import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandComposite;
import fr.mrcraftcod.gunterdiscord.commands.newconfig.guild.warns.DoubleWarnConfigurationCommand;
import fr.mrcraftcod.gunterdiscord.commands.newconfig.guild.warns.MegaWarnConfigurationCommand;
import fr.mrcraftcod.gunterdiscord.commands.newconfig.guild.warns.SimpleWarnConfigurationCommand;
import net.dv8tion.jda.api.entities.ChannelType;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class WarnsConfigurationCommandComposite extends CommandComposite{
	public WarnsConfigurationCommandComposite(@Nullable Command parent){
		super(parent);
		this.addSubCommand(new SimpleWarnConfigurationCommand(this));
		this.addSubCommand(new DoubleWarnConfigurationCommand(this));
		this.addSubCommand(new MegaWarnConfigurationCommand(this));
	}
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ADMIN;
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Warns";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("warns");
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return "Warns configurations";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
