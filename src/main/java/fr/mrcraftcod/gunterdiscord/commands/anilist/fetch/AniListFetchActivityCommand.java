package fr.mrcraftcod.gunterdiscord.commands.anilist.fetch;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.runners.anilist.AniListActivityScheduledRunner;
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
public class AniListFetchActivityCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	AniListFetchActivityCommand(@Nullable final Command parent){
		super(parent);
	}
	
	@Override
	@Nonnull
	public CommandResult execute(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		new AniListActivityScheduledRunner(event.getJDA()).run();
		return CommandResult.SUCCESS;
	}
	
	@Override
	@Nonnull
	public AccessLevel getAccessLevel(){
		return AccessLevel.MODERATOR;
	}
	
	
	@Nonnull
	@Override
	public String getName(){
		return "AniList fetch activity";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("activity", "a");
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return "Fetch user's activity from AniList";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
