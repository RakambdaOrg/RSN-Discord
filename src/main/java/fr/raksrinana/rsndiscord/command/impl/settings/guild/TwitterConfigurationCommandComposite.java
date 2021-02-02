package fr.raksrinana.rsndiscord.command.impl.settings.guild;

import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.CommandComposite;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.twitter.SearchChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.twitter.SearchesConfigurationCommand;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.twitter.UsersChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.twitter.UsersConfigurationCommand;
import fr.raksrinana.rsndiscord.permission.IPermission;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import static fr.raksrinana.rsndiscord.permission.PermissionUtils.ALLOW;

public class TwitterConfigurationCommandComposite extends CommandComposite{
	public TwitterConfigurationCommandComposite(Command parent){
		super(parent);
		addSubCommand(new UsersChannelConfigurationCommand(this));
		addSubCommand(new UsersConfigurationCommand(this));
		addSubCommand(new SearchChannelConfigurationCommand(this));
		addSubCommand(new SearchesConfigurationCommand(this));
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return ALLOW;
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return "Twitter";
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("twitter");
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return "Twitter configurations";
	}
}
