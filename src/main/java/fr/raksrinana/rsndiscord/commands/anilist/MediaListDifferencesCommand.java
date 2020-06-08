package fr.raksrinana.rsndiscord.commands.anilist;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.utils.anilist.media.MediaType;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;
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
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder embedBuilder){
		super.addHelp(guild, embedBuilder);
		embedBuilder.addField("filter", translate(guild, "command.anilist.media-list-differences.help.filter"), false);
		embedBuilder.addField("user1", translate(guild, "command.anilist.media-list-differences.help.user1"), false);
		embedBuilder.addField("user2", translate(guild, "command.anilist.media-list-differences.help.user2"), false);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		if(args.size() != 3 || event.getMessage().getMentionedUsers().size() < 2){
			return CommandResult.BAD_ARGUMENTS;
		}
		final var type = MediaType.valueOf(args.pop().toUpperCase());
		final var members = event.getMessage().getMentionedMembers();
		new MediaListDifferencesRunner(event.getJDA(), type, event.getChannel(), members.get(0), members.get(1)).runQueryOnDefaultUsersChannels();
		return CommandResult.SUCCESS;
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
