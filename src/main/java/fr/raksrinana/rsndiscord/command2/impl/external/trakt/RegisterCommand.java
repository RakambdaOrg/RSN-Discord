package fr.raksrinana.rsndiscord.command2.impl.external.trakt;

import fr.raksrinana.rsndiscord.api.trakt.TraktApi;
import fr.raksrinana.rsndiscord.api.trakt.requests.oauth.DeviceCodePostRequest;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.command2.base.group.SubCommand;
import fr.raksrinana.rsndiscord.command2.permission.IPermission;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.NotNull;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.command2.permission.SimplePermission.FALSE_BY_DEFAULT;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

public class RegisterCommand extends SubCommand{
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
	public IPermission getPermission(){
		return FALSE_BY_DEFAULT;
	}
	
	@Override
	@NotNull
	public CommandResult execute(@NotNull SlashCommandEvent event){
		var guild = event.getGuild();
		
		Settings.getGeneral().getTrakt()
				.getAccessToken(event.getUser().getIdLong())
				.ifPresentOrElse(userToken -> JDAWrappers.edit(event, translate(guild, "trakt.already-registered")).submit(),
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
		return SUCCESS;
	}
}
