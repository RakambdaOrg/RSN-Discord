package fr.raksrinana.rsndiscord.command.impl.birthday;

import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.command.base.group.SubCommand;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import static fr.raksrinana.rsndiscord.command.CommandResult.HANDLED;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

@Log4j2
public class AddCommand extends SubCommand{
	private static final String USER_OPTION_ID = "user";
	private static final String DATE_OPTION_ID = "date";
	
	@Override
	@NotNull
	public String getId(){
		return "add";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Add a user's birthday";
	}
	
	@Override
	@NotNull
	protected Collection<? extends OptionData> getOptions(){
		return List.of(
				new OptionData(OptionType.USER, USER_OPTION_ID, "User").setRequired(true),
				new OptionData(OptionType.STRING, DATE_OPTION_ID, "Birthday").setRequired(true));
	}
	
	@Override
	@NotNull
	public CommandResult executeGuild(@NotNull SlashCommandEvent event, @NotNull Guild guild, @NotNull Member member){
		var target = event.getOption(USER_OPTION_ID).getAsUser();
		var date = getOptionAs(event.getOption(DATE_OPTION_ID), this::parseDate);
		
		date.ifPresentOrElse(
						birthday -> {
							Settings.get(guild).getBirthdays().setBirthday(target, birthday);
							JDAWrappers.reply(event, translate(guild, "birthday.saved")).submit();
						},
						() -> JDAWrappers.reply(event, translate(guild, "birthday.bad-date")).submit());
		return HANDLED;
	}
	
	@Nullable
	private LocalDate parseDate(@Nullable String string){
		if(Objects.isNull(string)){
			return null;
		}
		
		try{
			return LocalDate.parse(string, ISO_LOCAL_DATE);
		}
		catch(DateTimeParseException e){
			log.error("Failed to parse date {}", string, e);
		}
		return null;
	}
}
