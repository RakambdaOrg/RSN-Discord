package fr.raksrinana.rsndiscord.commands.permissions;

import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import fr.raksrinana.rsndiscord.utils.permission.Permission;
import fr.raksrinana.rsndiscord.utils.permission.SimplePermission;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.util.List;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@BotCommand
public class PermissionsCommandComposite extends CommandComposite{
	/**
	 * Constructor.
	 */
	public PermissionsCommandComposite(){
		this.addSubCommand(new GrantCommand(this));
		this.addSubCommand(new DenyCommand(this));
		this.addSubCommand(new ResetCommand(this));
	}
	
	@Override
	public @NonNull Permission getPermission(){
		return new SimplePermission("command.permissions", false);
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.permissions.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("permissions", "perm");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.permissions.description");
	}
}