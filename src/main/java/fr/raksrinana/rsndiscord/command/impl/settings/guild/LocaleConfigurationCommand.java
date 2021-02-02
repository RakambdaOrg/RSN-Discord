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
import java.util.Locale;
import java.util.Optional;
import static fr.raksrinana.rsndiscord.permission.PermissionUtils.ALLOW;

public class LocaleConfigurationCommand extends ValueConfigurationCommand<Locale>{
	public LocaleConfigurationCommand(Command parent){
		super(parent);
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return ALLOW;
	}
	
	@Override
	protected Locale extractValue(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
		if(args.isEmpty()){
			throw new IllegalArgumentException("Please pass the language");
		}
		return new Locale(args.poll());
	}
	
	@Override
	protected void setConfig(@NotNull Guild guild, @NotNull Locale value){
		Settings.get(guild).setLocale(value);
	}
	
	@Override
	protected void removeConfig(@NotNull Guild guild){
		Settings.get(guild).setLocale(null);
	}
	
	@NotNull
	@Override
	protected Optional<Locale> getConfig(@NotNull Guild guild){
		return Settings.get(guild).getLocale();
	}
	
	@Override
	protected @NotNull String getValueName(){
		return "Locale";
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("locale");
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return "Locale";
	}
}
