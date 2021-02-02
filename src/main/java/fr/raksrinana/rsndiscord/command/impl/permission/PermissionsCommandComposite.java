package fr.raksrinana.rsndiscord.command.impl.permission;

import fr.raksrinana.rsndiscord.command.BotCommand;
import fr.raksrinana.rsndiscord.command.CommandComposite;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@BotCommand
public class PermissionsCommandComposite extends CommandComposite{
	/**
	 * Constructor.
	 */
	public PermissionsCommandComposite(){
		addSubCommand(new GrantCommand(this));
		addSubCommand(new DenyCommand(this));
		addSubCommand(new ResetCommand(this));
		addSubCommand(new ListCommand(this));
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return new SimplePermission("command.permissions", false);
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return translate(guild, "command.permissions.name");
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return translate(guild, "command.permissions.description");
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("permissions", "perm");
	}
}
