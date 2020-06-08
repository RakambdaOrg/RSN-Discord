package fr.raksrinana.rsndiscord.commands.anilist;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.settings.guild.schedule.AnilistAiringScheduleConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.anilist.airing.AiringSchedule;
import fr.raksrinana.rsndiscord.utils.anilist.queries.AiringSchedulePagedQuery;
import fr.raksrinana.rsndiscord.utils.schedule.ScheduleUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

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
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder embedBuilder){
		super.addHelp(guild, embedBuilder);
		embedBuilder.addField("id", translate(guild, "command.anilist.next-airing.help.id"), false);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		if(args.isEmpty()){
			return CommandResult.BAD_ARGUMENTS;
		}
		final int mediaId;
		try{
			mediaId = Integer.parseInt(args.pop());
		}
		catch(NumberFormatException e){
			return CommandResult.BAD_ARGUMENTS;
		}
		try{
			final var now = ZonedDateTime.now();
			final var schedules = new AiringSchedulePagedQuery(mediaId).getResult(event.getMember());
			schedules.stream().filter(schedule -> now.isBefore(schedule.getAiringAt())).min(Comparator.comparingInt(AiringSchedule::getTimeUntilAiring)).ifPresentOrElse(schedule -> {
				final var builder = new EmbedBuilder();
				schedule.fillEmbed(builder);
				ScheduleUtils.addScheduleAndNotify(new AnilistAiringScheduleConfiguration(event.getAuthor(), event.getChannel(), schedule.getDate(), schedule), event.getChannel());
			}, () -> Actions.reply(event, translate(event.getGuild(), "command.anilist.next-airing.execution.not-found"), null));
		}
		catch(Exception e){
			log.error("Failed to get airing schedule", e);
			return CommandResult.FAILED;
		}
		return CommandResult.SUCCESS;
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
