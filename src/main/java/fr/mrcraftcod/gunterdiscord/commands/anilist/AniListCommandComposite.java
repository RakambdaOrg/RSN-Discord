package fr.mrcraftcod.gunterdiscord.commands.anilist;

import fr.mrcraftcod.gunterdiscord.commands.anilist.fetch.AniListFetchCommandComposite;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandComposite;
import net.dv8tion.jda.api.entities.ChannelType;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-08.
 *
 * @author Thomas Couchoud
 * @since 2018-10-08
 */
public class AniListCommandComposite extends CommandComposite{
	/**
	 * Constructor.
	 */
	public AniListCommandComposite(){
		super();
		addSubCommand(new AniListFetchCommandComposite(this));
		addSubCommand(new AniListRegisterCommand(this));
		addSubCommand(new AniListGetUserEntryCommand(this));
	}
	
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@Override
	public String getName(){
		return "AniList";
	}
	
	@Override
	public List<String> getCommandStrings(){
		return List.of("anilist", "al");
	}
	
	@Override
	public String getDescription(){
		return "AniList related commands";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
