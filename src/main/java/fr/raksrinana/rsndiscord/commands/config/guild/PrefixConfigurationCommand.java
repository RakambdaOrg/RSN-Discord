package fr.raksrinana.rsndiscord.commands.config.guild;

import fr.raksrinana.rsndiscord.commands.config.helpers.ValueConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.settings.NewSettings;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class PrefixConfigurationCommand extends ValueConfigurationCommand<String>{
	public PrefixConfigurationCommand(@Nullable final Command parent){
		super(parent);
	}
	
	@Nonnull
	@Override
	protected Optional<String> getConfig(@Nonnull final Guild guild){
		return NewSettings.getConfiguration(guild).getPrefix();
	}
	
	@Override
	protected String getValueName(){
		return "Prefix";
	}
	
	@Override
	protected void setConfig(@Nonnull final Guild guild, @Nonnull final String value){
		NewSettings.getConfiguration(guild).setPrefix(value);
	}
	
	@Override
	protected String extractValue(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args){
		if(args.isEmpty()){
			throw new IllegalArgumentException("Please provide the value");
		}
		return args.pop();
	}
	
	@Override
	protected void removeConfig(@Nonnull final Guild guild){
		NewSettings.getConfiguration(guild).setPrefix(null);
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "DJ Role";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("djRole");
	}
}
