package fr.raksrinana.rsndiscord.commands.anilist.fetch;

import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import net.dv8tion.jda.api.entities.ChannelType;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-08.
 *
 * @author Thomas Couchoud
 * @since 2018-10-08
 */
public class AniListFetchCommandComposite extends CommandComposite{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	public AniListFetchCommandComposite(@Nullable final Command parent){
		super(parent);
		this.addSubCommand(new AniListFetchActivityCommand(this));
		this.addSubCommand(new AniListFetchNotificationCommand(this));
		this.addSubCommand(new AniListFetchMediaListCommand(this));
		this.addSubCommand(new AniListFetchMediaUserListDifferencesCommand(this));
	}
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.MODERATOR;
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "AniList fetcher";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("fetch", "f");
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return "Fetch data from AniList";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
