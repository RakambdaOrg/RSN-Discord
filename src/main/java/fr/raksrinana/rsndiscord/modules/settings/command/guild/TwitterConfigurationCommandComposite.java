package fr.raksrinana.rsndiscord.modules.settings.command.guild;

import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import fr.raksrinana.rsndiscord.modules.permission.IPermission;
import fr.raksrinana.rsndiscord.modules.settings.command.guild.twitter.SearchChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.modules.settings.command.guild.twitter.SearchesConfigurationCommand;
import fr.raksrinana.rsndiscord.modules.settings.command.guild.twitter.UsersChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.modules.settings.command.guild.twitter.UsersConfigurationCommand;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.util.List;
import static fr.raksrinana.rsndiscord.modules.permission.PermissionUtils.ALLOW;

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
