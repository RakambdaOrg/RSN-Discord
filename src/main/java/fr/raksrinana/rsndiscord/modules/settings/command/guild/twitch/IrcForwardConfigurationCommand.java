package fr.raksrinana.rsndiscord.modules.settings.command.guild.twitch;

import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.modules.permission.IPermission;
import fr.raksrinana.rsndiscord.modules.settings.Settings;
import fr.raksrinana.rsndiscord.modules.settings.command.helpers.BooleanConfigurationCommand;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.util.List;
import java.util.Optional;
import static fr.raksrinana.rsndiscord.modules.permission.PermissionUtils.ALLOW;

public class IrcForwardConfigurationCommand extends BooleanConfigurationCommand{
	public IrcForwardConfigurationCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return ALLOW;
	}
	
	@Override
	protected void setConfig(@NonNull final Guild guild, @NonNull final Boolean value){
		Settings.get(guild).getTwitchConfiguration().setIrcForward(value);
	}
	
	@Override
	protected void removeConfig(@NonNull final Guild guild){
		Settings.get(guild).getTwitchConfiguration().setIrcForward(false);
	}
	
	@NonNull
	@Override
	protected Optional<Boolean> getConfig(final Guild guild){
		return Optional.of(Settings.get(guild).getTwitchConfiguration().isIrcForward());
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return "IRC message forwarding";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("forward");
	}
}
