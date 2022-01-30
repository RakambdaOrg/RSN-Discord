package fr.raksrinana.rsndiscord.command.impl.birthday;

import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.command.base.group.SubCommand;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import static fr.raksrinana.rsndiscord.command.CommandResult.HANDLED;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

public class ListCommand extends SubCommand{
	private static final DateTimeFormatter DF = DateTimeFormatter.ISO_LOCAL_DATE;
	
	@Override
	@NotNull
	public String getId(){
		return "list";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "List birthdays of all users";
	}
	
	@Override
	@NotNull
	public CommandResult executeGuild(@NotNull SlashCommandInteractionEvent event, @NotNull Guild guild, @NotNull Member member){
		Settings.get(guild).getBirthdays()
				.getBirthdays()
				.forEach((key, birthday) -> key.getUser()
						.ifPresent(user -> {
							var message = translate(guild, "birthday.birthday",
									user.getAsMention(),
									birthday.getDate().format(DF),
									birthday.getDate().until(LocalDate.now()).normalized().getYears());
							
							JDAWrappers.reply(event, message).submit();
						}));
		return HANDLED;
	}
}
