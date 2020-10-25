package fr.raksrinana.rsndiscord.modules.settings.command.guild.nickname;

import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.modules.permission.IPermission;
import fr.raksrinana.rsndiscord.modules.settings.Settings;
import fr.raksrinana.rsndiscord.modules.settings.command.helpers.ValueConfigurationCommand;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import static fr.raksrinana.rsndiscord.modules.permission.PermissionUtils.ALLOW;

public class ChangeDelayConfigurationCommand extends ValueConfigurationCommand<Long>{
	public ChangeDelayConfigurationCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return ALLOW;
	}
	
	@Override
	protected Long extractValue(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		if(args.isEmpty()){
			throw new IllegalArgumentException("Please mention the delay");
		}
		try{
			return Long.parseLong(args.pop());
		}
		catch(final NumberFormatException e){
			throw new IllegalArgumentException("Please mention the delay");
		}
	}
	
	@Override
	protected void setConfig(@NonNull final Guild guild, @NonNull final Long value){
		Settings.get(guild).getNicknameConfiguration().setChangeDelay(value);
	}
	
	@Override
	protected void removeConfig(@NonNull final Guild guild){
	}
	
	@Override
	protected String getValueName(){
		return "Change delay";
	}
	
	@NonNull
	@Override
	protected Optional<Long> getConfig(final Guild guild){
		return Optional.of(Settings.get(guild).getNicknameConfiguration().getChangeDelay());
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return "Nickname change delay";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("changeDelay");
	}
}
