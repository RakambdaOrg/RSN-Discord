package fr.raksrinana.rsndiscord.modules.twitter.command;

import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import fr.raksrinana.rsndiscord.modules.permission.IPermission;
import fr.raksrinana.rsndiscord.modules.permission.SimplePermission;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.util.List;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@BotCommand
public class TwitterCommandComposite extends CommandComposite{
	/**
	 * Constructor.
	 */
	public TwitterCommandComposite(){
		this.addSubCommand(new FetchCommand(this));
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return new SimplePermission("command.twitter", false);
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.twitter.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("twitter");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.twitter.description");
	}
}
