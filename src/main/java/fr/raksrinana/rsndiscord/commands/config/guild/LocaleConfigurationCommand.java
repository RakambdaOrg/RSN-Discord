package fr.raksrinana.rsndiscord.commands.config.guild;

import fr.raksrinana.rsndiscord.commands.config.helpers.ValueConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.settings.Settings;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class LocaleConfigurationCommand extends ValueConfigurationCommand<Locale>{
	public LocaleConfigurationCommand(final Command parent){
		super(parent);
	}
	
	@Override
	protected Locale extractValue(@NonNull GuildMessageReceivedEvent event, @NonNull LinkedList<String> args){
		if(args.isEmpty()){
			throw new IllegalArgumentException("Please pass the language");
		}
		return new Locale(args.poll());
	}
	
	@Override
	protected void setConfig(@NonNull final Guild guild, @NonNull final Locale value){
		Settings.get(guild).setLocale(value);
	}
	
	@Override
	protected void removeConfig(@NonNull final Guild guild){
		Settings.get(guild).setLocale(Locale.ENGLISH);
	}
	
	@NonNull
	@Override
	protected Optional<Locale> getConfig(@NonNull final Guild guild){
		return Optional.ofNullable(Settings.get(guild).getLocale());
	}
	
	@Override
	protected String getValueName(){
		return "Locale";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("locale");
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return "Locale";
	}
}
