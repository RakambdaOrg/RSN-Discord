package fr.raksrinana.rsndiscord.command.impl.settings.guild.joinleave;

import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.impl.settings.helpers.SetConfigurationCommand;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.settings.Settings;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.util.*;
import static fr.raksrinana.rsndiscord.permission.PermissionUtils.ALLOW;

public class LeaveImagesConfigurationCommand extends SetConfigurationCommand<String>{
	public LeaveImagesConfigurationCommand(Command parent){
		super(parent);
	}
	
	@Override
	protected void removeConfig(@NotNull Guild guild, @NotNull String value){
		Settings.get(guild).getJoinLeaveConfiguration().getLeaveImages().remove(value);
	}
	
	@Override
	protected @NotNull String extractValue(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args) throws IllegalArgumentException{
		if(args.isEmpty()){
			throw new IllegalArgumentException("Please give a link");
		}
		return args.pop();
	}
	
	@NotNull
	@Override
	protected Optional<Set<String>> getConfig(@NotNull Guild guild){
		return Optional.of(Settings.get(guild).getJoinLeaveConfiguration().getLeaveImages());
	}
	
	@Override
	protected void createConfig(@NotNull Guild guild, @NotNull String value){
		var set = new HashSet<String>();
		set.add(value);
		Settings.get(guild).getJoinLeaveConfiguration().setLeaveImages(set);
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return ALLOW;
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return "Leave images";
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("leaveImages");
	}
}
