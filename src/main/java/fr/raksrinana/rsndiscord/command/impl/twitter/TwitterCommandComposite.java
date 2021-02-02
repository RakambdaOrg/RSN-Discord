package fr.raksrinana.rsndiscord.command.impl.twitter;

import fr.raksrinana.rsndiscord.command.BotCommand;
import fr.raksrinana.rsndiscord.command.CommandComposite;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@BotCommand
public class TwitterCommandComposite extends CommandComposite{
	/**
	 * Constructor.
	 */
	public TwitterCommandComposite(){
		addSubCommand(new FetchCommand(this));
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return new SimplePermission("command.twitter", false);
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return translate(guild, "command.twitter.name");
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return translate(guild, "command.twitter.description");
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("twitter");
	}
}
