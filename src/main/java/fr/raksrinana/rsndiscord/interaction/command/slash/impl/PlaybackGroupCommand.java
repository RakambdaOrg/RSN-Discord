package fr.raksrinana.rsndiscord.interaction.command.slash.impl;

import fr.raksrinana.rsndiscord.interaction.command.slash.api.BotSlashCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.base.group.SubCommandsGroupSlashCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.impl.playback.StartCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.impl.playback.StopCommand;
import org.jetbrains.annotations.NotNull;

@BotSlashCommand
public class PlaybackGroupCommand extends SubCommandsGroupSlashCommand{
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
