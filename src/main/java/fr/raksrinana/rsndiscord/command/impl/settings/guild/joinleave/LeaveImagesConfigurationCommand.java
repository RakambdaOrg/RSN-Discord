package fr.raksrinana.rsndiscord.command.impl.settings.guild.joinleave;

import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.impl.settings.helpers.SetConfigurationCommand;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.settings.Settings;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.*;
import static fr.raksrinana.rsndiscord.permission.PermissionUtils.ALLOW;

public class LeaveImagesConfigurationCommand extends SetConfigurationCommand<String>{
	public LeaveImagesConfigurationCommand(final Command parent){
		super(parent);
	}
	
	@Override
	protected void removeConfig(@NonNull Guild guild, @NonNull String value){
		Settings.get(guild).getJoinLeaveConfiguration().getLeaveImages().remove(value);
	}
	
	@Override
	protected @NonNull String extractValue(@NonNull GuildMessageReceivedEvent event, @NonNull LinkedList<String> args) throws IllegalArgumentException{
		if(args.isEmpty()){
			throw new IllegalArgumentException("Please give a link");
		}
		return args.pop();
	}
	
	@NonNull
	@Override
	protected Optional<Set<String>> getConfig(@NonNull final Guild guild){
		return Optional.of(Settings.get(guild).getJoinLeaveConfiguration().getLeaveImages());
	}
	
	@Override
	protected void createConfig(@NonNull Guild guild, @NonNull String value){
		final var set = new HashSet<String>();
		set.add(value);
		Settings.get(guild).getJoinLeaveConfiguration().setLeaveImages(set);
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return ALLOW;
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return "Leave images";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("leaveImages");
	}
}
