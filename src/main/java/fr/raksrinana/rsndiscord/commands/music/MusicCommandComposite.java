package fr.raksrinana.rsndiscord.commands.music;

import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import lombok.NonNull;
import java.util.List;

@BotCommand
public class MusicCommandComposite extends CommandComposite{
	/**
	 * Constructor.
	 */
	public MusicCommandComposite(){
		this.addSubCommand(new AddMusicCommand(this));
		this.addSubCommand(new StopMusicCommand(this));
		this.addSubCommand(new PauseMusicCommand(this));
		this.addSubCommand(new ResumeMusicCommand(this));
		this.addSubCommand(new NowPlayingMusicCommand(this));
		this.addSubCommand(new SeekMusicCommand(this));
		this.addSubCommand(new SkipMusicCommand(this));
		this.addSubCommand(new QueueMusicCommand(this));
		this.addSubCommand(new ShuffleMusicCommand(this));
		this.addSubCommand(new MoveMusicCommand(this));
		this.addSubCommand(new VolumeMusicCommand(this));
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Music";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("music", "m");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Handles music interactions";
	}
}
