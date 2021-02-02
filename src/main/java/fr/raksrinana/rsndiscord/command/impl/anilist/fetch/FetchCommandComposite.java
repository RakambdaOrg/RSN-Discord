package fr.raksrinana.rsndiscord.command.impl.anilist.fetch;

import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.CommandComposite;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

public class FetchCommandComposite extends CommandComposite{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	public FetchCommandComposite(Command parent){
		super(parent);
		addSubCommand(new ActivityCommand(this));
		addSubCommand(new NotificationCommand(this));
		addSubCommand(new MediaListCommand(this));
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return new SimplePermission("command.anilist.fetch", false);
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return translate(guild, "command.anilist.fetch.name");
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return translate(guild, "command.anilist.fetch.description");
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("fetch", "f");
	}
}
