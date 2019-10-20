package fr.raksrinana.rsndiscord.commands.anilist;

import fr.raksrinana.rsndiscord.commands.anilist.fetch.AniListFetchCommandComposite;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import net.dv8tion.jda.api.entities.ChannelType;
import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-08.
 *
 * @author Thomas Couchoud
 * @since 2018-10-08
 */
@BotCommand
public class AniListCommandComposite extends CommandComposite{
	/**
	 * Constructor.
	 */
	public AniListCommandComposite(){
		super();
		this.addSubCommand(new AniListFetchCommandComposite(this));
		this.addSubCommand(new AniListRegisterCommand(this));
		this.addSubCommand(new AniListGetUserEntryCommand(this));
	}
	
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	
	@Nonnull
	@Override
	public String getName(){
		return "AniList";
	}
	
	@Nonnull
	
	@Override
	public List<String> getCommandStrings(){
		return List.of("anilist", "al");
	}
	
	@Nonnull
	
	@Override
	public String getDescription(){
		return "AniList related commands";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
