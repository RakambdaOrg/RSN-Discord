package fr.raksrinana.rsndiscord.commands.anilist;

import fr.raksrinana.rsndiscord.commands.anilist.fetch.FetchCommandComposite;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import fr.raksrinana.rsndiscord.utils.permission.Permission;
import fr.raksrinana.rsndiscord.utils.permission.SimplePermission;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.util.List;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@BotCommand
public class AniListCommandComposite extends CommandComposite{
	/**
	 * Constructor.
	 */
	public AniListCommandComposite(){
		super();
		this.addSubCommand(new FetchCommandComposite(this));
		this.addSubCommand(new RegisterCommand(this));
		this.addSubCommand(new MediaListDifferencesCommand(this));
		this.addSubCommand(new NextAiringCommand(this));
		this.addSubCommand(new InfoCommand(this));
	}
	
	@Override
	public @NonNull Permission getPermission(){
		return new SimplePermission("command.anilist", true);
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.anilist.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("anilist", "al");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.anilist.description");
	}
}
