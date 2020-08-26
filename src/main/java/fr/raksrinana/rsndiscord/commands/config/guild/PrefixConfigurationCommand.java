package fr.raksrinana.rsndiscord.commands.config.guild;

import fr.raksrinana.rsndiscord.commands.config.helpers.ValueConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.permission.Permission;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import static fr.raksrinana.rsndiscord.utils.permission.PermissionUtils.ALLOW;

public class PrefixConfigurationCommand extends ValueConfigurationCommand<String>{
	public PrefixConfigurationCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public @NonNull Permission getPermission(){
		return ALLOW;
	}
	
	@Override
	protected String extractValue(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		if(args.isEmpty()){
			throw new IllegalArgumentException("Please provide the value");
		}
		return args.pop();
	}
	
	@Override
	protected void setConfig(@NonNull final Guild guild, @NonNull final String value){
		Settings.get(guild).setPrefix(value);
	}
	
	@Override
	protected void removeConfig(@NonNull final Guild guild){
		Settings.get(guild).setPrefix(null);
	}
	
	@Override
	protected String getValueName(){
		return "Prefix";
	}
	
	@NonNull
	@Override
	protected Optional<String> getConfig(@NonNull final Guild guild){
		return Settings.get(guild).getPrefix();
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return "Prefix";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("prefix");
	}
}
