package fr.raksrinana.rsndiscord.command2.impl.external.twitch;

import fr.raksrinana.rsndiscord.api.twitch.TwitchUtils;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.command2.base.group.SubCommand;
import fr.raksrinana.rsndiscord.command2.permission.IPermission;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.List;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.command2.permission.SimplePermission.FALSE_BY_DEFAULT;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

public class ConnectCommand extends SubCommand{
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
	public CommandResult execute(@NotNull SlashCommandEvent event){
		var user = event.getOption(USER_OPTION_ID).getAsString();
		
		try{
			TwitchUtils.connect(event.getGuild(), user);
			JDAWrappers.replyCommand(event, "OK").submitAndDelete(5);
		}
		catch(Exception e){
			Log.getLogger(event.getGuild()).warn("Missing configuration for IRC", e);
			JDAWrappers.replyCommand(event, translate(event.getGuild(), "twitch.not-configured")).submitAndDelete(5);
		}
		return SUCCESS;
	}
}
