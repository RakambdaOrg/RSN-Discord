package fr.raksrinana.rsndiscord.interaction.command.slash.impl.external.twitch;

import fr.raksrinana.rsndiscord.api.twitch.TwitchUtils;
import fr.raksrinana.rsndiscord.interaction.command.CommandResult;
import fr.raksrinana.rsndiscord.interaction.command.slash.base.group.SubSlashCommand;
import fr.raksrinana.rsndiscord.interaction.command.permission.IPermission;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.List;
import static fr.raksrinana.rsndiscord.interaction.command.CommandResult.HANDLED;
import static fr.raksrinana.rsndiscord.interaction.command.permission.SimplePermission.FALSE_BY_DEFAULT;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

@Log4j2
public class ConnectCommand extends SubSlashCommand{
	private static final String USER_OPTION_ID = "user";
	
	@Override
	@NotNull
	public String getId(){
		return "leave";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Leave a channel";
	}
	
	@Override
	@NotNull
	public IPermission getPermission(){
		return FALSE_BY_DEFAULT;
	}
	
	@Override
	@NotNull
	protected Collection<? extends OptionData> getOptions(){
		return List.of(new OptionData(STRING, USER_OPTION_ID, "Username").setRequired(true));
	}
	
	@Override
	@NotNull
	public CommandResult executeGuild(@NotNull SlashCommandInteraction event, @NotNull Guild guild, @NotNull Member member){
		var user = event.getOption(USER_OPTION_ID).getAsString();
		
		try{
			TwitchUtils.connect(guild, user);
			JDAWrappers.edit(event, "OK").submitAndDelete(5);
		}
		catch(Exception e){
			log.warn("Missing configuration for IRC", e);
			JDAWrappers.edit(event, translate(guild, "twitch.not-configured")).submitAndDelete(5);
		}
		return HANDLED;
	}
}
