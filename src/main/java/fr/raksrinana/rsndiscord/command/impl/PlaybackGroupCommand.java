package fr.raksrinana.rsndiscord.command.impl;

import fr.raksrinana.rsndiscord.command.BotSlashCommand;
import fr.raksrinana.rsndiscord.command.base.group.SubCommandsGroupCommand;
import fr.raksrinana.rsndiscord.command.impl.playback.StartCommand;
import fr.raksrinana.rsndiscord.command.impl.playback.StopCommand;
import org.jetbrains.annotations.NotNull;

@BotSlashCommand
public class PlaybackGroupCommand extends SubCommandsGroupCommand{
	public PlaybackGroupCommand(){
		addSubcommand(new StartCommand());
		addSubcommand(new StopCommand());
	}
	
	@Override
	@NotNull
	public String getId(){
		return "playback";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Playback control";
	}
	
	@Override
	public boolean getDefaultPermission(){
		return false;
	}
}
