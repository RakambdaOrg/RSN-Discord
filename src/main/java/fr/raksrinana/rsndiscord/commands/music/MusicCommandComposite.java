package fr.raksrinana.rsndiscord.commands.music;

import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.util.List;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

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
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.music.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("music", "m");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.music.description");
	}
}
