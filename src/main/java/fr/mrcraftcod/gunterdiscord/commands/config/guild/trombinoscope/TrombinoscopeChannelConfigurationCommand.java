package fr.mrcraftcod.gunterdiscord.commands.config.guild.trombinoscope;

import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.config.helpers.ChannelConfigurationCommand;
import fr.mrcraftcod.gunterdiscord.settings.NewSettings;
import fr.mrcraftcod.gunterdiscord.settings.types.ChannelConfiguration;
import net.dv8tion.jda.api.entities.Guild;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class TrombinoscopeChannelConfigurationCommand extends ChannelConfigurationCommand{
	public TrombinoscopeChannelConfigurationCommand(@Nullable Command parent){
		super(parent);
	}
	
	@Override
	protected void removeConfig(@Nonnull Guild guild){
		NewSettings.getConfiguration(guild).getTrombinoscopeConfiguration().setPhotoChannel(null);
	}
	
	@Nonnull
	@Override
	protected Optional<ChannelConfiguration> getConfig(@Nonnull Guild guild){
		return NewSettings.getConfiguration(guild).getTrombinoscopeConfiguration().getPhotoChannel();
	}
	
	@Override
	protected void setConfig(@Nonnull Guild guild, @Nonnull ChannelConfiguration value){
		NewSettings.getConfiguration(guild).getTrombinoscopeConfiguration().setPhotoChannel(value);
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Trombinoscope photo channel";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("photoChannel");
	}
}
