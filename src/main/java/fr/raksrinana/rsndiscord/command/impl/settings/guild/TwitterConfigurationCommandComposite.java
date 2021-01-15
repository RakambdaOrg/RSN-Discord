package fr.raksrinana.rsndiscord.command.impl.settings.guild;

import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.CommandComposite;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.twitter.SearchChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.twitter.SearchesConfigurationCommand;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.twitter.UsersChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.twitter.UsersConfigurationCommand;
import fr.raksrinana.rsndiscord.permission.IPermission;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.util.List;
import static fr.raksrinana.rsndiscord.permission.PermissionUtils.ALLOW;

public class TwitterConfigurationCommandComposite extends CommandComposite{
	public TwitterConfigurationCommandComposite(final Command parent){
		super(parent);
		this.addSubCommand(new UsersChannelConfigurationCommand(this));
		this.addSubCommand(new UsersConfigurationCommand(this));
		this.addSubCommand(new SearchChannelConfigurationCommand(this));
		this.addSubCommand(new SearchesConfigurationCommand(this));
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return ALLOW;
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return "Twitter";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("twitter");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return "Twitter configurations";
	}
}
