package fr.raksrinana.rsndiscord.command.impl.external.anilist;

import fr.raksrinana.rsndiscord.api.anilist.AniListApi;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.command.base.group.SubCommand;
import fr.raksrinana.rsndiscord.utils.InvalidResponseException;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.NotNull;
import static fr.raksrinana.rsndiscord.command.CommandResult.FAILED;
import static fr.raksrinana.rsndiscord.command.CommandResult.HANDLED;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@Log4j2
public class RegisterCommand extends SubCommand{
	private static final String TOKEN_OPTION_ID = "token";
	
	@Override
	@NotNull
	public String getId(){
		return "register";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Register your anilist account";
	}
	
	@Override
	@NotNull
	public CommandResult execute(@NotNull SlashCommandEvent event){
		var guild = event.getGuild();
		
		var token = event.getOption(TOKEN_OPTION_ID).getAsString();
		
		try{
			AniListApi.requestToken(event.getMember(), token);
			JDAWrappers.edit(event, translate(guild, "anilist.api-code.saved")).submitAndDelete(10);
		}
		catch(IllegalArgumentException e){
			JDAWrappers.edit(event, translate(guild, "anilist.api-code.invalid")).submitAndDelete(10);
			return HANDLED;
		}
		catch(InvalidResponseException e){
			log.error("Error getting AniList access token", e);
			Utilities.reportException("Error getting AniList Token", e);
			
			JDAWrappers.edit(event, translate(guild, "anilist.api-code.save-error")).submitAndDelete(10);
			return FAILED;
		}
		return HANDLED;
	}
}
