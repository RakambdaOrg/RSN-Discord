package fr.raksrinana.rsndiscord.command.impl.anilist;

import fr.raksrinana.rsndiscord.api.anilist.data.media.MediaType;
import fr.raksrinana.rsndiscord.command.BasicCommand;
import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.command.CommandResult.BAD_ARGUMENTS;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

class MediaListDifferencesCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	MediaListDifferencesCommand(Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NotNull Guild guild, @NotNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("filter", translate(guild, "command.anilist.media-list-differences.help.filter"), false)
				.addField("user1", translate(guild, "command.anilist.media-list-differences.help.user1"), false)
				.addField("user2", translate(guild, "command.anilist.media-list-differences.help.user2"), false);
	}
	
	@NotNull
	@Override
	public CommandResult execute(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
		super.execute(event, args);
		var message = event.getMessage();
		
		if(args.size() != 3 || message.getMentionedUsers().size() < 2){
			return BAD_ARGUMENTS;
		}
		
		var type = MediaType.valueOf(args.pop().toUpperCase());
		var members = message.getMentionedMembers();
		
		new MediaListDifferencesRunner(event.getJDA(), type, event.getChannel(), members.get(0), members.get(1)).runQueryOnDefaultUsersChannels();
		return SUCCESS;
	}
	
	@NotNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + "<filter> <@user1> <@user2>";
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return new SimplePermission("command.anilist.media-list-differences", false);
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return translate(guild, "command.anilist.media-list-differences.name");
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return translate(guild, "command.anilist.media-list-differences.description");
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("differences", "diff", "d");
	}
}
