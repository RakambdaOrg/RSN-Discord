package fr.raksrinana.rsndiscord.command2.impl.birthday;

import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.command2.base.group.SubCommand;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.impl.guild.birthday.Birthday;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import static fr.raksrinana.rsndiscord.command.CommandResult.HANDLED;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

public class GetCommand extends SubCommand{
	private static final DateTimeFormatter DF = DateTimeFormatter.ISO_LOCAL_DATE;
	private static final String USER_OPTION_ID = "user";
	
	@Override
	@NotNull
	public String getId(){
		return "get";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Get a user's birthday";
	}
	
	@Override
	@NotNull
	protected Collection<? extends OptionData> getOptions(){
		return List.of(new OptionData(OptionType.USER, USER_OPTION_ID, "User").setRequired(true));
	}
	
	@Override
	@NotNull
	public CommandResult execute(@NotNull SlashCommandEvent event){
		var guild = event.getGuild();
		var user = event.getOption(USER_OPTION_ID).getAsUser();
		
		Settings.get(guild).getBirthdays()
				.getBirthday(user)
				.map(Birthday::getDate)
				.ifPresentOrElse(
						date -> {
							var message = translate(guild, "birthday.birthday",
									user.getAsMention(),
									date.format(DF),
									date.until(LocalDate.now()).normalized().getYears());
							JDAWrappers.reply(event, message).submit();
						},
						() -> JDAWrappers.reply(event, translate(guild, "birthday.unknown-date")).submit());
		return HANDLED;
	}
}
