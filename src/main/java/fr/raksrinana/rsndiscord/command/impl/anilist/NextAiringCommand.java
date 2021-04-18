package fr.raksrinana.rsndiscord.command.impl.anilist;

import fr.raksrinana.rsndiscord.api.anilist.data.airing.AiringSchedule;
import fr.raksrinana.rsndiscord.api.anilist.query.AiringSchedulePagedQuery;
import fr.raksrinana.rsndiscord.command.BasicCommand;
import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import fr.raksrinana.rsndiscord.settings.guild.anilist.AniListAiringScheduleConfiguration;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.command.CommandResult.*;
import static fr.raksrinana.rsndiscord.schedule.ScheduleUtils.addScheduleAndNotify;
import static fr.raksrinana.rsndiscord.schedule.ScheduleUtils.deleteMessage;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.util.Comparator.comparingInt;

@Slf4j
class NextAiringCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	NextAiringCommand(Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NotNull Guild guild, @NotNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("id", translate(guild, "command.anilist.next-airing.help.id"), false);
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
		var channel = event.getChannel();
		
		try{
			var now = ZonedDateTime.now();
			var schedules = new AiringSchedulePagedQuery(mediaId.get()).getResult(event.getMember());
			
			schedules.stream().filter(schedule -> now.isBefore(schedule.getAiringAt()))
					.min(comparingInt(AiringSchedule::getTimeUntilAiring))
					.ifPresentOrElse(schedule -> {
						var builder = new EmbedBuilder();
						schedule.fillEmbed(guild, builder);
						
						var airingScheduleConfiguration = new AniListAiringScheduleConfiguration(
								event.getAuthor(), channel, schedule.getDate(), schedule);
						addScheduleAndNotify(airingScheduleConfiguration, channel);
					}, () -> JDAWrappers.message(channel, translate(guild, "anilist.airing-schedule-not-found")).submit()
							.thenAccept(deleteMessage(date -> date.plusMinutes(5))));
		}
		catch(Exception e){
			log.error("Failed to get airing schedule", e);
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
		return new SimplePermission("command.anilist.next-airing", true);
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return translate(guild, "command.anilist.next-airing.name");
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return translate(guild, "command.anilist.next-airing.description");
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("nextairing", "na");
	}
}
