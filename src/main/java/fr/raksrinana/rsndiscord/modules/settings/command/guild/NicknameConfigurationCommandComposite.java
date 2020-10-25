package fr.raksrinana.rsndiscord.modules.settings.command.guild;

import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import fr.raksrinana.rsndiscord.modules.permission.IPermission;
import fr.raksrinana.rsndiscord.modules.settings.command.guild.nickname.ChangeDelayConfigurationCommand;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.util.List;
import static fr.raksrinana.rsndiscord.modules.permission.PermissionUtils.ALLOW;

public class NicknameConfigurationCommandComposite extends CommandComposite{
	public NicknameConfigurationCommandComposite(final Command parent){
		super(parent);
		this.addSubCommand(new ChangeDelayConfigurationCommand(this));
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return ALLOW;
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return "Nickname";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("nickname");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return "Nickname configurations";
	}
}
