package fr.raksrinana.rsndiscord.commands.anilist.fetch;

import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import lombok.NonNull;
import java.util.List;

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
	
	@NonNull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ADMIN;
	}
	
	@NonNull
	@Override
	public String getName(){
		return "AniList fetcher";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("fetch", "f");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Fetch data";
	}
}
