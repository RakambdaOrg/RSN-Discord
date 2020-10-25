package fr.raksrinana.rsndiscord.modules.settings.command.guild.twitch;

import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.modules.permission.IPermission;
import fr.raksrinana.rsndiscord.modules.settings.Settings;
import fr.raksrinana.rsndiscord.modules.settings.command.helpers.SetConfigurationCommand;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.*;
import static fr.raksrinana.rsndiscord.modules.permission.PermissionUtils.ALLOW;

public class AutoConnectUsersConfigurationCommand extends SetConfigurationCommand<String>{
	public AutoConnectUsersConfigurationCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return ALLOW;
	}
	
	@NonNull
	@Override
	protected Optional<Set<String>> getConfig(@NonNull final Guild guild){
		return Optional.of(Settings.get(guild).getTwitchConfiguration().getTwitchAutoConnectUsers());
	}
	
	@NonNull
	@Override
	protected String extractValue(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args) throws IllegalArgumentException{
		if(args.isEmpty()){
			throw new IllegalArgumentException("Please mention a user");
		}
		return args.pop();
	}
	
	@Override
	protected void removeConfig(@NonNull final Guild guild, @NonNull final String value){
		Settings.get(guild).getTwitchConfiguration().getTwitchAutoConnectUsers().remove(value);
	}
	
	@Override
	protected void createConfig(@NonNull final Guild guild, @NonNull final String value){
		final var set = new HashSet<String>();
		set.add(value);
		Settings.get(guild).getTwitchConfiguration().setTwitchAutoConnectUsers(set);
	}
	
	@Override
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("User", "The user to add or remove", false);
	}
	
	@NonNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " [user]";
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return "Users auto reconnect";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("autoReconnect");
	}
}
