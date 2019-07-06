package fr.mrcraftcod.gunterdiscord.commands.config.guild;

import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.config.helpers.ValueConfigurationCommand;
import fr.mrcraftcod.gunterdiscord.settings.NewSettings;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class PrefixConfigurationCommand extends ValueConfigurationCommand<String>{
	public PrefixConfigurationCommand(@Nullable Command parent){
		super(parent);
	}
	
	@Override
	protected void removeConfig(@Nonnull Guild guild){
		NewSettings.getConfiguration(guild).setPrefix(null);
	}
	
	@Nonnull
	@Override
	protected Optional<String> getConfig(@Nonnull Guild guild){
		return NewSettings.getConfiguration(guild).getPrefix();
	}
	
	@Override
	protected String getValueName(){
		return "Prefix";
	}
	
	@Override
	protected void setConfig(@Nonnull Guild guild, @Nonnull String value){
		NewSettings.getConfiguration(guild).setPrefix(value);
	}
	
	@Override
	protected String extractValue(@Nonnull GuildMessageReceivedEvent event, @Nonnull LinkedList<String> args){
		if(args.isEmpty()){
			throw new IllegalArgumentException("Please provide the value");
		}
		return args.pop();
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
