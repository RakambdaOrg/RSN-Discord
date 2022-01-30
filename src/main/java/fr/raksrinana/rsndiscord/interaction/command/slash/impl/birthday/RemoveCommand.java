package fr.raksrinana.rsndiscord.interaction.command.slash.impl.birthday;

import fr.raksrinana.rsndiscord.interaction.command.CommandResult;
import fr.raksrinana.rsndiscord.interaction.command.slash.base.group.SubSlashCommand;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.List;
import static fr.raksrinana.rsndiscord.interaction.command.CommandResult.HANDLED;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

public class RemoveCommand extends SubSlashCommand{
	private static final String USER_OPTION_ID = "user";
	
	@Override
	@NotNull
	public String getId(){
		return "delete";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Remove a user's birthday";
	}
	
	@Override
	@NotNull
	protected Collection<? extends OptionData> getOptions(){
		return List.of(new OptionData(OptionType.USER, USER_OPTION_ID, "User").setRequired(true));
	}
	
	@Override
	@NotNull
	public CommandResult executeGuild(@NotNull SlashCommandInteraction event, @NotNull Guild guild, @NotNull Member member){
		var target = event.getOption(USER_OPTION_ID).getAsUser();
		
		Settings.get(guild).getBirthdays().removeBirthday(target);
		JDAWrappers.reply(event, translate(guild, "birthday.removed")).submit();
		return HANDLED;
	}
}
