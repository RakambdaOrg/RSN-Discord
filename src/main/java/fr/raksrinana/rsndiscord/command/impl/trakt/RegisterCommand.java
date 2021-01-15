package fr.raksrinana.rsndiscord.command.impl.trakt;

import fr.raksrinana.rsndiscord.api.trakt.TraktApi;
import fr.raksrinana.rsndiscord.api.trakt.requests.oauth.DeviceCodePostRequest;
import fr.raksrinana.rsndiscord.command.BasicCommand;
import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import fr.raksrinana.rsndiscord.settings.Settings;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.command.CommandResult.FAILED;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

class RegisterCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	RegisterCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return new SimplePermission("command.trakt.register", true);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		var guild = event.getGuild();
		
		if(args.isEmpty()){
			Settings.getGeneral().getTrakt()
					.getAccessToken(event.getAuthor().getIdLong())
					.ifPresentOrElse(userToken -> event.getChannel().sendMessage(translate(guild, "trakt.already-registered")).submit(),
							() -> {
								try{
									var deviceCode = TraktApi.postQuery(null, new DeviceCodePostRequest());
									var content = translate(guild, "trakt.register-url", deviceCode.getVerificationUrl(), deviceCode.getUserCode());
									event.getChannel().sendMessage(content).submit();
									TraktApi.pollDeviceToken(event, deviceCode);
								}
								catch(Exception e){
									throw new RuntimeException("Failed to get an authentication device code", e);
								}
							});
			return SUCCESS;
		}
		return FAILED;
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.trakt.register.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("register", "r");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.trakt.register.description");
	}
}
