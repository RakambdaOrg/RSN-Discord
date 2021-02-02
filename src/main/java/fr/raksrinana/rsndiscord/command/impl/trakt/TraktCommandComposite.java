package fr.raksrinana.rsndiscord.command.impl.trakt;

import fr.raksrinana.rsndiscord.command.BotCommand;
import fr.raksrinana.rsndiscord.command.CommandComposite;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@BotCommand
public class TraktCommandComposite extends CommandComposite{
	/**
	 * Constructor.
	 */
	public TraktCommandComposite(){
		super();
		addSubCommand(new RegisterCommand(this));
		addSubCommand(new HistoryCommand(this));
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return new SimplePermission("command.trakt", true);
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return translate(guild, "command.trakt.name");
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return translate(guild, "command.trakt.description");
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("trakt");
	}
}
