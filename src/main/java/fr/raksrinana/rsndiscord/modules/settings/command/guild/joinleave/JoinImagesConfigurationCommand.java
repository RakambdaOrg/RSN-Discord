package fr.raksrinana.rsndiscord.modules.settings.command.guild.joinleave;

import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.modules.permission.IPermission;
import fr.raksrinana.rsndiscord.modules.settings.Settings;
import fr.raksrinana.rsndiscord.modules.settings.command.helpers.SetConfigurationCommand;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.*;
import static fr.raksrinana.rsndiscord.modules.permission.PermissionUtils.ALLOW;

public class JoinImagesConfigurationCommand extends SetConfigurationCommand<String>{
	public JoinImagesConfigurationCommand(final Command parent){
		super(parent);
	}
	
	@Override
	protected void removeConfig(@NonNull Guild guild, @NonNull String value){
		Settings.get(guild).getJoinLeaveConfiguration().getJoinImages().remove(value);
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
		return Optional.of(Settings.get(guild).getJoinLeaveConfiguration().getJoinImages());
	}
	
	@Override
	protected void createConfig(@NonNull Guild guild, @NonNull String value){
		final var set = new HashSet<String>();
		set.add(value);
		Settings.get(guild).getJoinLeaveConfiguration().setJoinImages(set);
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return ALLOW;
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return "Join images";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("joinImages");
	}
}
