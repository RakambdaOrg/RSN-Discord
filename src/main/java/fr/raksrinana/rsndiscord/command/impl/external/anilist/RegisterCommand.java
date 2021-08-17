package fr.raksrinana.rsndiscord.command.impl.external.anilist;

import fr.raksrinana.rsndiscord.api.anilist.AniListApi;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.command.base.group.SubCommand;
import fr.raksrinana.rsndiscord.utils.InvalidResponseException;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import static fr.raksrinana.rsndiscord.command.CommandResult.FAILED;
import static fr.raksrinana.rsndiscord.command.CommandResult.HANDLED;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

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
	protected @NotNull Collection<? extends OptionData> getOptions(){
		return List.of(new OptionData(STRING, TOKEN_OPTION_ID, "Token").setRequired(false));
	}
	
	@Override
	@NotNull
	public CommandResult execute(@NotNull SlashCommandEvent event){
		var guild = event.getGuild();
		
		var token = Optional.ofNullable(event.getOption(TOKEN_OPTION_ID)).map(OptionMapping::getAsString);
		
		try{
			token.ifPresentOrElse(code -> {
				AniListApi.requestToken(event.getMember(), code);
				JDAWrappers.edit(event, translate(guild, "anilist.api-code.saved")).submitAndDelete(10);
			}, () -> JDAWrappers.edit(event, "API token can be obtained from: %s".formatted(AniListApi.getCODE_LINK())).submit());
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
