package fr.raksrinana.rsndiscord.command2.impl.moderation;

import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.command2.base.group.SubCommand;
import fr.raksrinana.rsndiscord.settings.guild.schedule.UnbanScheduleConfiguration;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.schedule.ScheduleUtils.addSchedule;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static fr.raksrinana.rsndiscord.utils.Utilities.durationToString;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;
import static net.dv8tion.jda.api.interactions.commands.OptionType.USER;

public class SoftBanCommand extends SubCommand{
	private static final String USER_OPTION_ID = "user";
	private static final String DURATION_OPTION_ID = "duration";
	private static final String REASON_OPTION_ID = "reason";
	
	@Override
	@NotNull
	public String getId(){
		return "softban";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Ban a person for a certain amount of time";
	}
	
	@Override
	protected @NotNull Collection<? extends OptionData> getOptions(){
		return List.of(
				new OptionData(USER, USER_OPTION_ID, "The user to soft ban").setRequired(true),
				new OptionData(STRING, DURATION_OPTION_ID, "The duration of the ban").setRequired(true),
				new OptionData(STRING, REASON_OPTION_ID, "The reason of the ban").setRequired(true));
	}
	
	@Override
	@NotNull
	public CommandResult execute(@NotNull SlashCommandEvent event){
		var guild = event.getGuild();
		var channel = event.getChannel();
		
		var target = event.getOption(USER_OPTION_ID).getAsMember();
		var duration = getOptionAs(event.getOption(DURATION_OPTION_ID), Utilities::parseDuration).orElseThrow();
		var reason = event.getOption(REASON_OPTION_ID).getAsString();
		
		var message = translate(guild, "softban.banned", target.getAsMention(), durationToString(duration), reason);
		
		var unbanScheduleConfiguration = new UnbanScheduleConfiguration(event.getUser(),
				ZonedDateTime.now().plus(duration), "Banned for: " + reason, target.getId());
		
		JDAWrappers.ban(target, 0, reason).sumbit()
				.thenAccept(empty -> {
					addSchedule(guild, unbanScheduleConfiguration);
					JDAWrappers.replyCommand(event, message).submit();
				});
		return SUCCESS;
	}
}
