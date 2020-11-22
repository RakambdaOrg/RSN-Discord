package fr.raksrinana.rsndiscord.modules.anilist.command;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.modules.anilist.data.media.MediaType;
import fr.raksrinana.rsndiscord.modules.permission.IPermission;
import fr.raksrinana.rsndiscord.modules.permission.SimplePermission;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.commands.generic.CommandResult.BAD_ARGUMENTS;
import static fr.raksrinana.rsndiscord.commands.generic.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

class MediaListDifferencesCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	MediaListDifferencesCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("filter", translate(guild, "command.anilist.media-list-differences.help.filter"), false)
				.addField("user1", translate(guild, "command.anilist.media-list-differences.help.user1"), false)
				.addField("user2", translate(guild, "command.anilist.media-list-differences.help.user2"), false);
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return new SimplePermission("command.anilist.media-list-differences", false);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		var message = event.getMessage();
		
		if(args.size() != 3 || message.getMentionedUsers().size() < 2){
			return BAD_ARGUMENTS;
		}
		
		final var type = MediaType.valueOf(args.pop().toUpperCase());
		final var members = message.getMentionedMembers();
		
		new MediaListDifferencesRunner(event.getJDA(), type, event.getChannel(), members.get(0), members.get(1)).runQueryOnDefaultUsersChannels();
		return SUCCESS;
	}
	
	@NonNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + "<filter> <@user1> <@user2>";
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.anilist.media-list-differences.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("differences", "diff", "d");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.anilist.media-list-differences.description");
	}
}
