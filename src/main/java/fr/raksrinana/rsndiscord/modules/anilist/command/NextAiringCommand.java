package fr.raksrinana.rsndiscord.modules.anilist.command;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.modules.anilist.config.AniListAiringScheduleConfiguration;
import fr.raksrinana.rsndiscord.modules.anilist.data.airing.AiringSchedule;
import fr.raksrinana.rsndiscord.modules.anilist.query.AiringSchedulePagedQuery;
import fr.raksrinana.rsndiscord.modules.permission.IPermission;
import fr.raksrinana.rsndiscord.modules.permission.SimplePermission;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.commands.generic.CommandResult.*;
import static fr.raksrinana.rsndiscord.modules.schedule.ScheduleUtils.addScheduleAndNotify;
import static fr.raksrinana.rsndiscord.modules.schedule.ScheduleUtils.deleteMessage;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.util.Comparator.comparingInt;

@Slf4j
class NextAiringCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	NextAiringCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("id", translate(guild, "command.anilist.next-airing.help.id"), false);
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return new SimplePermission("command.anilist.next-airing", true);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
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
					}, () -> channel.sendMessage(translate(guild, "anilist.airing-schedule-not-found")).submit()
							.thenAccept(deleteMessage(date -> date.plusMinutes(5))));
		}
		catch(Exception e){
			log.error("Failed to get airing schedule", e);
			return FAILED;
		}
		return SUCCESS;
	}
	
	@NonNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + "<id>";
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.anilist.next-airing.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("nextairing", "na");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.anilist.next-airing.description");
	}
}
