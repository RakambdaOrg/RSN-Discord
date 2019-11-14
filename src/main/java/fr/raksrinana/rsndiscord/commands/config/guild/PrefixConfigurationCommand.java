package fr.raksrinana.rsndiscord.commands.config.guild;

import fr.raksrinana.rsndiscord.commands.config.helpers.ValueConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.settings.Settings;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class PrefixConfigurationCommand extends ValueConfigurationCommand<String>{
	public PrefixConfigurationCommand(final Command parent){
		super(parent);
	}
	
	@Override
	protected String extractValue(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		if(args.isEmpty()){
			throw new IllegalArgumentException("Please provide the value");
		}
		return args.pop();
	}
	
	@Override
	protected String getValueName(){
		return "Prefix";
	}
	
	@Override
	protected void setConfig(@NonNull final Guild guild, @NonNull final String value){
		Settings.get(guild).setPrefix(value);
	}
	
	@Override
	protected void removeConfig(@NonNull final Guild guild){
		Settings.get(guild).setPrefix(null);
	}
	
	@NonNull
	@Override
	protected Optional<String> getConfig(@NonNull final Guild guild){
		return Settings.get(guild).getPrefix();
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Prefix";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("prefix");
	}
}
