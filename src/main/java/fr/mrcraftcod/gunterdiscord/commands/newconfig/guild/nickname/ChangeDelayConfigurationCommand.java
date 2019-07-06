package fr.mrcraftcod.gunterdiscord.commands.newconfig.guild.nickname;

import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.newconfig.helpers.ValueConfigurationCommand;
import fr.mrcraftcod.gunterdiscord.newSettings.NewSettings;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class ChangeDelayConfigurationCommand extends ValueConfigurationCommand<Long>{
	public ChangeDelayConfigurationCommand(Command parent){
		super(parent);
	}
	
	@Nonnull
	@Override
	protected Optional<Long> getConfig(Guild guild){
		return Optional.of(NewSettings.getConfiguration(guild).getNicknameConfiguration().getChangeDelay());
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Nickname change delay";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("changeDelay");
	}
	
	@Override
	protected String getValueName(){
		return "Change delay";
	}
	
	@Override
	protected void setConfig(@Nonnull Guild guild, @Nonnull Long value){
		NewSettings.getConfiguration(guild).getNicknameConfiguration().setChangeDelay(value);
	}
	
	@Override
	protected Long extractValue(@Nonnull GuildMessageReceivedEvent event, @Nonnull LinkedList<String> args){
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
	protected void removeConfig(@Nonnull Guild guild){
	}
}
