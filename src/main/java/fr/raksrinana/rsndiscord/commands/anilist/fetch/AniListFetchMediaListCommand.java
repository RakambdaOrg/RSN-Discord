package fr.raksrinana.rsndiscord.commands.anilist.fetch;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.runners.anilist.AniListMediaListScheduledRunner;
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
public class AniListFetchMediaListCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	AniListFetchMediaListCommand(@Nullable final Command parent){
		super(parent);
	}
	
	@Nonnull
	@Override
	public CommandResult execute(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		new AniListMediaListScheduledRunner(event.getJDA()).run();
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
		return "AniList fetch media user list";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		//noinspection SpellCheckingInspection
		return List.of("medialist", "m");
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return "Fetch media user list changes from AniList";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
