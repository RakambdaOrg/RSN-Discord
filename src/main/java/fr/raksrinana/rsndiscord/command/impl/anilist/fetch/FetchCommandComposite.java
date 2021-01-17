package fr.raksrinana.rsndiscord.command.impl.anilist.fetch;

import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.CommandComposite;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.util.List;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

public class FetchCommandComposite extends CommandComposite{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	public FetchCommandComposite(final Command parent){
		super(parent);
		this.addSubCommand(new ActivityCommand(this));
		this.addSubCommand(new NotificationCommand(this));
		this.addSubCommand(new MediaListCommand(this));
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return new SimplePermission("command.anilist.fetch", false);
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.anilist.fetch.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("fetch", "f");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.anilist.fetch.description");
	}
}