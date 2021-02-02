package fr.raksrinana.rsndiscord.command.impl.settings.guild.twitch;

import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.impl.settings.helpers.BooleanConfigurationCommand;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.settings.Settings;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Optional;
import static fr.raksrinana.rsndiscord.permission.PermissionUtils.ALLOW;

public class IrcForwardConfigurationCommand extends BooleanConfigurationCommand{
	public IrcForwardConfigurationCommand(Command parent){
		super(parent);
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return ALLOW;
	}
	
	@Override
	protected void setConfig(@NotNull Guild guild, @NotNull Boolean value){
		Settings.get(guild).getTwitchConfiguration().setIrcForward(value);
	}
	
	@Override
	protected void removeConfig(@NotNull Guild guild){
		Settings.get(guild).getTwitchConfiguration().setIrcForward(false);
	}
	
	@NotNull
	@Override
	protected Optional<Boolean> getConfig(Guild guild){
		return Optional.of(Settings.get(guild).getTwitchConfiguration().isIrcForward());
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return "IRC message forwarding";
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("forward");
	}
}
