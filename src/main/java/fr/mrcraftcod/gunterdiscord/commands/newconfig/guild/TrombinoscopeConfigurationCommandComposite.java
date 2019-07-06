package fr.mrcraftcod.gunterdiscord.commands.newconfig.guild;

import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandComposite;
import fr.mrcraftcod.gunterdiscord.commands.newconfig.guild.trombinoscope.TrombinoscopeChannelConfigurationCommand;
import fr.mrcraftcod.gunterdiscord.commands.newconfig.guild.trombinoscope.TrombinoscopeRoleConfigurationCommand;
import net.dv8tion.jda.api.entities.ChannelType;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class TrombinoscopeConfigurationCommandComposite extends CommandComposite{
	public TrombinoscopeConfigurationCommandComposite(@Nullable Command parent){
		super(parent);
		this.addSubCommand(new TrombinoscopeChannelConfigurationCommand(this));
		this.addSubCommand(new TrombinoscopeRoleConfigurationCommand(this));
	}
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ADMIN;
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Trombinoscope";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("trombinoscope");
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return "Trombinoscope configurations";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
