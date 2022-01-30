package fr.raksrinana.rsndiscord.interaction.command.slash.impl.bot;

import fr.raksrinana.rsndiscord.interaction.command.CommandResult;
import fr.raksrinana.rsndiscord.interaction.command.slash.base.group.SubSlashCommand;
import fr.raksrinana.rsndiscord.interaction.command.permission.IPermission;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.List;
import static fr.raksrinana.rsndiscord.interaction.command.CommandResult.HANDLED_NO_MESSAGE;
import static fr.raksrinana.rsndiscord.interaction.command.permission.CreatorPermission.CREATOR_PERMISSION;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

public class SayCommand extends SubSlashCommand{
	private static final String MESSAGE_OPTION_ID = "message";
	
	@Override
	@NotNull
	public String getId(){
		return "say";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Make the bot say something";
	}
	
	@Override
	@NotNull
	public IPermission getPermission(){
		return CREATOR_PERMISSION;
	}
	
	@Override
	@NotNull
	protected Collection<? extends OptionData> getOptions(){
		return List.of(new OptionData(STRING, MESSAGE_OPTION_ID, "Message to send").setRequired(true));
	}
	
	@Override
	@NotNull
	public CommandResult executeGuild(@NotNull SlashCommandInteractionEvent event, @NotNull Guild guild, @NotNull Member member){
		var message = event.getOption(MESSAGE_OPTION_ID).getAsString();
		JDAWrappers.message(event.getTextChannel(), message).submit();
		return HANDLED_NO_MESSAGE;
	}
}
