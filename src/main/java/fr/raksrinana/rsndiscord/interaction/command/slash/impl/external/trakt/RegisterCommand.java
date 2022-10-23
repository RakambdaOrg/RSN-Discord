package fr.raksrinana.rsndiscord.interaction.command.slash.impl.external.trakt;

import fr.raksrinana.rsndiscord.api.trakt.TraktApi;
import fr.raksrinana.rsndiscord.api.trakt.requests.oauth.DeviceCodePostRequest;
import fr.raksrinana.rsndiscord.interaction.command.CommandResult;
import fr.raksrinana.rsndiscord.interaction.command.slash.base.group.SubSlashCommand;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.jetbrains.annotations.NotNull;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

public class RegisterCommand extends SubSlashCommand{
	@Override
	@NotNull
	public String getId(){
		return "register";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Register your trakt account";
	}
	
	@Override
	@NotNull
	public CommandResult executeGuild(@NotNull SlashCommandInteraction event, @NotNull Guild guild, @NotNull Member member){
		Settings.getGeneral().getTrakt()
				.getAccessToken(event.getUser().getIdLong())
				.ifPresentOrElse(
						userToken -> JDAWrappers.edit(event, translate(guild, "trakt.already-registered")).submit(),
						() -> {
							try{
								var deviceCode = TraktApi.postQuery(null, new DeviceCodePostRequest());
								var content = translate(guild, "trakt.register-url", deviceCode.getVerificationUrl(), deviceCode.getUserCode());
								JDAWrappers.edit(event, content).submit();
								TraktApi.pollDeviceToken(event, deviceCode);
							}
							catch(Exception e){
								throw new RuntimeException("Failed to get an authentication device code", e);
							}
						});
		return CommandResult.HANDLED;
	}
}
