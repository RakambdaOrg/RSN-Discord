package fr.raksrinana.rsndiscord.command.impl.settings.guild;

import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.impl.settings.helpers.ValueConfigurationCommand;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.settings.Settings;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import static fr.raksrinana.rsndiscord.permission.PermissionUtils.ALLOW;

public class LeaveServerBanDurationConfigurationCommand extends ValueConfigurationCommand<Duration>{
	public LeaveServerBanDurationConfigurationCommand(Command parent){
		super(parent);
	}
	
	@Override
	protected Duration extractValue(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
		if(args.isEmpty()){
			throw new IllegalArgumentException("Please pass the language");
		}
		try{
			var millis = Long.parseLong(args.poll());
			return Duration.ofMillis(millis);
		}
		catch(NumberFormatException e){
			throw new IllegalArgumentException("Please input a number", e);
		}
	}
	
	@Override
	protected void setConfig(@NotNull Guild guild, @NotNull Duration value){
		Settings.get(guild).setLeaveServerBanDuration(value);
	}
	
	@Override
	protected void removeConfig(@NotNull Guild guild){
		Settings.get(guild).setLeaveServerBanDuration(null);
	}
	
	@NotNull
	@Override
	protected Optional<Duration> getConfig(@NotNull Guild guild){
		return Settings.get(guild).getLeaveServerBanDuration();
	}
	
	@Override
	protected @NotNull String getValueName(){
		return "LeaveServerBanDuration";
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return ALLOW;
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return "Leave server ban duration";
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("leaveServerBanDuration");
	}
}
