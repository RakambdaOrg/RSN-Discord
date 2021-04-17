package fr.raksrinana.rsndiscord.command.impl.anilist;

import fr.raksrinana.rsndiscord.api.anilist.query.MediaPagedQuery;
import fr.raksrinana.rsndiscord.command.BasicCommand;
import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.command.CommandResult.*;
import static fr.raksrinana.rsndiscord.schedule.ScheduleUtils.deleteMessage;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@Slf4j
class InfoCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	InfoCommand(Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NotNull Guild guild, @NotNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("id", translate(guild, "command.anilist.info.help.id"), false);
	}
	
	@NotNull
	@Override
	public CommandResult execute(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
		super.execute(event, args);
		if(args.isEmpty()){
			return BAD_ARGUMENTS;
		}
		
		var mediaId = getArgumentAsInteger(args);
		if(mediaId.isEmpty()){
			return BAD_ARGUMENTS;
		}
		
		var guild = event.getGuild();
		
		try{
			var medias = new MediaPagedQuery(mediaId.get()).getResult(event.getMember());
			if(!medias.isEmpty()){
				medias.forEach(media -> {
					var builder = new EmbedBuilder();
					media.fillEmbed(guild, builder);
					JDAWrappers.message(event, builder.build()).submit();
				});
			}
			else{
				JDAWrappers.message(event, translate(guild, "anilist.media-not-found")).submit()
						.thenAccept(deleteMessage(date -> date.plusMinutes(5)));
			}
		}
		catch(Exception e){
			log.error("Failed to get media with id {}", mediaId, e);
			return FAILED;
		}
		return SUCCESS;
	}
	
	@NotNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + "<id>";
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return new SimplePermission("command.anilist.info", true);
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return translate(guild, "command.anilist.info.name");
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return translate(guild, "command.anilist.info.description");
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("info");
	}
}
