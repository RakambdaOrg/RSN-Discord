package fr.mrcraftcod.gunterdiscord.commands.overwatch;

import fr.mrcraftcod.gunterdiscord.commands.generic.CommandComposite;
import net.dv8tion.jda.api.entities.ChannelType;
import javax.annotation.Nonnull;
import java.util.List;

public class OverwatchCommandComposite extends CommandComposite{
	public OverwatchCommandComposite(){
		this.addSubCommand(new OverwatchGetWeekMatchesCommand(this));
		this.addSubCommand(new OverwatchGetMatchCommand(this));
	}
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Overwatch";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("overwatch", "ow");
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return "Overwatch related commands";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
