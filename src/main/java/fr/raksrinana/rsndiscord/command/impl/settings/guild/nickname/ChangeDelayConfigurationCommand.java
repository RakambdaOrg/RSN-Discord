package fr.raksrinana.rsndiscord.command.impl.settings.guild.nickname;

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

public class ChangeDelayConfigurationCommand extends ValueConfigurationCommand<Long>{
	public ChangeDelayConfigurationCommand(Command parent){
		super(parent);
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return ALLOW;
	}
	
	@Override
	protected Long extractValue(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
		if(args.isEmpty()){
			throw new IllegalArgumentException("Please mention the delay");
		}
		try{
			return Long.parseLong(args.pop());
		}
		catch(NumberFormatException e){
			throw new IllegalArgumentException("Please mention the delay");
		}
	}
	
	@Override
	protected void setConfig(@NotNull Guild guild, @NotNull Long value){
		Settings.get(guild).getNicknameConfiguration().setChangeDelay(value);
	}
	
	@Override
	protected void removeConfig(@NotNull Guild guild){
	}
	
	@Override
	protected @NotNull String getValueName(){
		return "Change delay";
	}
	
	@NotNull
	@Override
	protected Optional<Long> getConfig(Guild guild){
		return Optional.of(Settings.get(guild).getNicknameConfiguration().getChangeDelay());
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return "Nickname change delay";
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("changeDelay");
	}
}
