package fr.raksrinana.rsndiscord.commands.overwatch;

import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import net.dv8tion.jda.api.entities.ChannelType;
import javax.annotation.Nonnull;
import java.util.List;

@BotCommand
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
