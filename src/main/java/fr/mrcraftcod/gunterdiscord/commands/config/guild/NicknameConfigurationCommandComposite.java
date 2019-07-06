package fr.mrcraftcod.gunterdiscord.commands.config.guild;

import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandComposite;
import fr.mrcraftcod.gunterdiscord.commands.config.guild.nickname.ChangeDelayConfigurationCommand;
import net.dv8tion.jda.api.entities.ChannelType;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class NicknameConfigurationCommandComposite extends CommandComposite{
	public NicknameConfigurationCommandComposite(@Nullable Command parent){
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
