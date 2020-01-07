package fr.raksrinana.rsndiscord.commands.anilist;

import fr.raksrinana.rsndiscord.commands.anilist.fetch.FetchCommandComposite;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import lombok.NonNull;
import java.util.List;

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
	}
	
	@NonNull
	@Override
	public String getName(){
		return "AniList";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("anilist", "al");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "AniList related commands";
	}
}
