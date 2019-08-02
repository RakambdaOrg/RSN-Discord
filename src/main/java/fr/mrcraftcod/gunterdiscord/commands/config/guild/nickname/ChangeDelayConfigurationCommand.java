package fr.mrcraftcod.gunterdiscord.commands.config.guild.nickname;

import fr.mrcraftcod.gunterdiscord.commands.config.helpers.ValueConfigurationCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.settings.NewSettings;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class ChangeDelayConfigurationCommand extends ValueConfigurationCommand<Long>{
	public ChangeDelayConfigurationCommand(final Command parent){
		super(parent);
	}
	
	@Nonnull
	@Override
	protected Optional<Long> getConfig(final Guild guild){
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
	protected void setConfig(@Nonnull final Guild guild, @Nonnull final Long value){
		NewSettings.getConfiguration(guild).getNicknameConfiguration().setChangeDelay(value);
	}
	
	@Override
	protected Long extractValue(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args){
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
	protected void removeConfig(@Nonnull final Guild guild){
	}
}
