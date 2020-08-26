package fr.raksrinana.rsndiscord.commands.action;

import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import fr.raksrinana.rsndiscord.utils.permission.Permission;
import fr.raksrinana.rsndiscord.utils.permission.SimplePermission;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.util.List;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@BotCommand
public class ActionCommandComposite extends CommandComposite{
	/**
	 * Constructor.
	 */
	public ActionCommandComposite(){
		super();
		this.addSubCommand(new SlapActionCommand(this));
	}
	
	@Override
	public @NonNull Permission getPermission(){
		return new SimplePermission("command.action", false);
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.action.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("action", "ac");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.action.description");
	}
}
