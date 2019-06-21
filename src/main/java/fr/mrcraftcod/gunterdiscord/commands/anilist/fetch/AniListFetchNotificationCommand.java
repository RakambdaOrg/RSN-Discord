package fr.mrcraftcod.gunterdiscord.commands.anilist.fetch;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.runners.anilist.AniListNotificationScheduledRunner;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-08.
 *
 * @author Thomas Couchoud
 * @since 2018-10-08
 */
public class AniListFetchNotificationCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	AniListFetchNotificationCommand(@Nullable final Command parent){
		super(parent);
	}
	
	
	@Nonnull
	@Override
	public CommandResult execute( @Nonnull final GuildMessageReceivedEvent event,  @Nonnull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		new AniListNotificationScheduledRunner(event.getJDA()).run();
		return CommandResult.SUCCESS;
	}
	
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.MODERATOR;
	}
	
	
	@Nonnull
	@Override
	public String getName(){
		return "AniList fetch notification";
	}
	
	@Nonnull
	
	@Override
	public List<String> getCommandStrings(){
		return List.of("notification", "n");
	}
	
	@Nonnull
	
	@Override
	public String getDescription(){
		return "Fetch notifications from AniList";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
