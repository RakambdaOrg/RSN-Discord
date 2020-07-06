package fr.raksrinana.rsndiscord.commands.config.guild;

import fr.raksrinana.rsndiscord.commands.config.guild.trakt.MediaChangeChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.config.guild.trakt.ThaChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.config.guild.trakt.ThaUserConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.util.List;

public class TraktConfigurationCommandComposite extends CommandComposite{
	public TraktConfigurationCommandComposite(final Command parent){
		super(parent);
		this.addSubCommand(new ThaChannelConfigurationCommand(this));
		this.addSubCommand(new ThaUserConfigurationCommand(this));
		this.addSubCommand(new MediaChangeChannelConfigurationCommand(this));
	}
	
	@NonNull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ADMIN;
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return "Trakt";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("trakt");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return "Trakt configurations";
	}
}
