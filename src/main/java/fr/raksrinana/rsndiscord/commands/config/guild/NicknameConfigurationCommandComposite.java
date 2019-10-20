package fr.raksrinana.rsndiscord.commands.config.guild;

import fr.raksrinana.rsndiscord.commands.config.guild.nickname.ChangeDelayConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import net.dv8tion.jda.api.entities.ChannelType;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class NicknameConfigurationCommandComposite extends CommandComposite{
	public NicknameConfigurationCommandComposite(@Nullable final Command parent){
		super(parent);
		this.addSubCommand(new ChangeDelayConfigurationCommand(this));
	}
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ADMIN;
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Nickname";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("nickname");
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return "Nickname configurations";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
