package fr.raksrinana.rsndiscord.commands.anilist;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.settings.guild.ReminderConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.anilist.airing.AiringSchedule;
import fr.raksrinana.rsndiscord.utils.anilist.queries.AiringSchedulePagedQuery;
import fr.raksrinana.rsndiscord.utils.reminder.AnilistReleaseReminderHandler;
import fr.raksrinana.rsndiscord.utils.reminder.ReminderTag;
import fr.raksrinana.rsndiscord.utils.reminder.ReminderUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
		embedBuilder.addField("id", "The id of the media on AniList", false);
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
			final var now = LocalDateTime.now();
			final var schedules = new AiringSchedulePagedQuery(mediaId).getResult(event.getMember());
			schedules.stream().filter(schedule -> now.isBefore(schedule.getAiringAt())).min(Comparator.comparingInt(AiringSchedule::getTimeUntilAiring)).ifPresentOrElse(schedule -> {
				final var builder = new EmbedBuilder();
				schedule.fillEmbed(builder);
				ReminderUtils.addReminderAndNotify(new ReminderConfiguration(event.getAuthor(), event.getChannel(), schedule.getDate(), MessageFormat.format("Episode {0} is airing", schedule.getEpisode()), ReminderTag.ANILIST_AIRING_SCHEDULE, Map.of(AnilistReleaseReminderHandler.MEDIA_ID_KEY, Integer.toString(mediaId))), event.getChannel());
			}, () -> Actions.reply(event, "No information on the next airing for media", null));
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
	public String getName(){
		return "Next airing";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("nextairing", "na");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Sets a reminder for the next aired media";
	}
}
