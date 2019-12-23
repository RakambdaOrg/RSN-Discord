package fr.raksrinana.rsndiscord.commands.trakt;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.trakt.TraktUtils;
import fr.raksrinana.rsndiscord.utils.trakt.requests.oauth.DeviceCodePostRequest;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

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
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("Code", "The code obtained after doing the first step (run this command without this parameter)", false);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		if(args.isEmpty()){
			Settings.get(event.getGuild()).getTraktConfiguration().getAccessToken(event.getAuthor().getIdLong()).ifPresentOrElse(userToken -> Actions.reply(event, "You're already registered", null), () -> {
				try{
					final var deviceCode = TraktUtils.postQuery(null, new DeviceCodePostRequest());
					Actions.reply(event, MessageFormat.format("Please visit {0} and enter this code: `{1}`.", deviceCode.getVerificationUrl(), deviceCode.getUserCode()), null);
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
	public String getCommandUsage(){
		return super.getCommandUsage() + " [code]";
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Trakt registering";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("register", "r");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Register your Trakt account";
	}
}
