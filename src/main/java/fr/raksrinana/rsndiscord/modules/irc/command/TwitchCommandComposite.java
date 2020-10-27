package fr.raksrinana.rsndiscord.modules.irc.command;

import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import fr.raksrinana.rsndiscord.modules.permission.IPermission;
import fr.raksrinana.rsndiscord.modules.permission.SimplePermission;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.util.List;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@BotCommand
public class TwitchCommandComposite extends CommandComposite{
	/**
	 * Constructor.
	 */
	public TwitchCommandComposite(){
		this.addSubCommand(new ConnectCommand(this));
		this.addSubCommand(new DisconnectCommand(this));
		this.addSubCommand(new QuitCommand(this));
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return new SimplePermission("command.twitch", false);
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.twitch.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("twitch", "tw");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.twitch.description");
	}
}