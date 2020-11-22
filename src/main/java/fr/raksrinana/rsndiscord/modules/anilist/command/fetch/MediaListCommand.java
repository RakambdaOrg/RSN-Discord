package fr.raksrinana.rsndiscord.modules.anilist.command.fetch;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.modules.anilist.runner.AniListMediaListRunner;
import fr.raksrinana.rsndiscord.modules.permission.IPermission;
import fr.raksrinana.rsndiscord.modules.permission.SimplePermission;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.commands.generic.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

class MediaListCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	MediaListCommand(final Command parent){
		super(parent);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		new AniListMediaListRunner(event.getJDA()).execute();
		return SUCCESS;
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return new SimplePermission("command.anilist.fetch.media-list", false);
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.anilist.fetch.media-list.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("medialist", "m");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.anilist.fetch.media-list.description");
	}
}
