package fr.raksrinana.rsndiscord.modules.settings.command.guild;

import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.modules.permission.IPermission;
import fr.raksrinana.rsndiscord.modules.settings.Settings;
import fr.raksrinana.rsndiscord.modules.settings.command.helpers.ValueConfigurationCommand;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import static fr.raksrinana.rsndiscord.modules.permission.PermissionUtils.ALLOW;

public class LocaleConfigurationCommand extends ValueConfigurationCommand<Locale>{
	public LocaleConfigurationCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return ALLOW;
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
		Settings.get(guild).setLocale(null);
	}
	
	@NonNull
	@Override
	protected Optional<Locale> getConfig(@NonNull final Guild guild){
		return Settings.get(guild).getLocale();
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
