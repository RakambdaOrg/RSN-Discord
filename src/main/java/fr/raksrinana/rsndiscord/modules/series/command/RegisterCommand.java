package fr.raksrinana.rsndiscord.modules.series.command;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.modules.permission.IPermission;
import fr.raksrinana.rsndiscord.modules.permission.SimplePermission;
import fr.raksrinana.rsndiscord.modules.series.trakt.TraktUtils;
import fr.raksrinana.rsndiscord.modules.series.trakt.requests.oauth.DeviceCodePostRequest;
import fr.raksrinana.rsndiscord.modules.settings.Settings;
import fr.raksrinana.rsndiscord.utils.Actions;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;
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
		if(args.isEmpty()){
			Settings.getGeneral().getTrakt()
					.getAccessToken(event.getAuthor().getIdLong())
					.ifPresentOrElse(userToken -> Actions.reply(event, translate(event.getGuild(), "trakt.already-registered"), null),
							() -> {
								try{
									final var deviceCode = TraktUtils.postQuery(null, new DeviceCodePostRequest());
									Actions.reply(event, translate(event.getGuild(), "trakt.register-url", deviceCode.getVerificationUrl(), deviceCode.getUserCode()), null);
									TraktUtils.pollDeviceToken(event, deviceCode);
								}
								catch(Exception e){
									throw new RuntimeException("Failed to get an authentication device code", e);
								}
							});
			return CommandResult.SUCCESS;
		}
		return CommandResult.FAILED;
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
