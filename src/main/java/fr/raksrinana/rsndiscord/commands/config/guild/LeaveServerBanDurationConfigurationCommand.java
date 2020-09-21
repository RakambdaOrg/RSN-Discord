package fr.raksrinana.rsndiscord.commands.config.guild;

import fr.raksrinana.rsndiscord.commands.config.helpers.ValueConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.permission.Permission;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import static fr.raksrinana.rsndiscord.utils.permission.PermissionUtils.ALLOW;

public class LeaveServerBanDurationConfigurationCommand extends ValueConfigurationCommand<Duration>{
	public LeaveServerBanDurationConfigurationCommand(final Command parent){
		super(parent);
	}
	
	@Override
	protected Duration extractValue(@NonNull GuildMessageReceivedEvent event, @NonNull LinkedList<String> args){
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
	protected void setConfig(@NonNull final Guild guild, @NonNull final Duration value){
		Settings.get(guild).setLeaveServerBanDuration(value);
	}
	
	@Override
	protected void removeConfig(@NonNull final Guild guild){
		Settings.get(guild).setLeaveServerBanDuration(null);
	}
	
	@NonNull
	@Override
	protected Optional<Duration> getConfig(@NonNull final Guild guild){
		return Settings.get(guild).getLeaveServerBanDuration();
	}
	
	@Override
	protected String getValueName(){
		return "LeaveServerBanDuration";
	}
	
	@Override
	public @NonNull Permission getPermission(){
		return ALLOW;
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return "Leave server ban duration";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("leaveServerBanDuration");
	}
}
