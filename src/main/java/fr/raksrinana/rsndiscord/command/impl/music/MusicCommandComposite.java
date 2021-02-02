package fr.raksrinana.rsndiscord.command.impl.music;

import fr.raksrinana.rsndiscord.command.BotCommand;
import fr.raksrinana.rsndiscord.command.CommandComposite;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@BotCommand
public class MusicCommandComposite extends CommandComposite{
	/**
	 * Constructor.
	 */
	public MusicCommandComposite(){
		addSubCommand(new AddMusicCommand(this));
		addSubCommand(new StopMusicCommand(this));
		addSubCommand(new PauseMusicCommand(this));
		addSubCommand(new ResumeMusicCommand(this));
		addSubCommand(new NowPlayingMusicCommand(this));
		addSubCommand(new SeekMusicCommand(this));
		addSubCommand(new SkipMusicCommand(this));
		addSubCommand(new QueueMusicCommand(this));
		addSubCommand(new ShuffleMusicCommand(this));
		addSubCommand(new MoveMusicCommand(this));
		addSubCommand(new VolumeMusicCommand(this));
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return new SimplePermission("command.music", false);
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return translate(guild, "command.music.name");
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return translate(guild, "command.music.description");
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("music", "m");
	}
}
