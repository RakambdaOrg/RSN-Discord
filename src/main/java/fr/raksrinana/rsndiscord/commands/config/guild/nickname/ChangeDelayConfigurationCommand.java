package fr.raksrinana.rsndiscord.commands.config.guild.nickname;

import fr.raksrinana.rsndiscord.commands.config.helpers.ValueConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.settings.Settings;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class ChangeDelayConfigurationCommand extends ValueConfigurationCommand<Long>{
	public ChangeDelayConfigurationCommand(final Command parent){
		super(parent);
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
	protected String getValueName(){
		return "Change delay";
	}
	
	@Override
	protected void setConfig(@NonNull final Guild guild, @NonNull final Long value){
		Settings.get(guild).getNicknameConfiguration().setChangeDelay(value);
	}
	
	@Override
	protected void removeConfig(@NonNull final Guild guild){
	}
	
	@NonNull
	@Override
	protected Optional<Long> getConfig(final Guild guild){
		return Optional.of(Settings.get(guild).getNicknameConfiguration().getChangeDelay());
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Nickname change delay";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("changeDelay");
	}
}
