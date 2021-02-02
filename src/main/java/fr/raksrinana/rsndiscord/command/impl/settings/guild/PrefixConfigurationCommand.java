package fr.raksrinana.rsndiscord.command.impl.settings.guild;

import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.impl.settings.helpers.ValueConfigurationCommand;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.settings.Settings;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import static fr.raksrinana.rsndiscord.permission.PermissionUtils.ALLOW;

public class PrefixConfigurationCommand extends ValueConfigurationCommand<String>{
	public PrefixConfigurationCommand(Command parent){
		super(parent);
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return ALLOW;
	}
	
	@Override
	protected String extractValue(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
		if(args.isEmpty()){
			throw new IllegalArgumentException("Please provide the value");
		}
		return args.pop();
	}
	
	@Override
	protected void setConfig(@NotNull Guild guild, @NotNull String value){
		Settings.get(guild).setPrefix(value);
	}
	
	@Override
	protected void removeConfig(@NotNull Guild guild){
		Settings.get(guild).setPrefix(null);
	}
	
	@Override
	protected @NotNull String getValueName(){
		return "Prefix";
	}
	
	@NotNull
	@Override
	protected Optional<String> getConfig(@NotNull Guild guild){
		return Settings.get(guild).getPrefix();
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return "Prefix";
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("prefix");
	}
}
