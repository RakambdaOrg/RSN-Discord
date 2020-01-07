package fr.raksrinana.rsndiscord.commands.music;

import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import fr.raksrinana.rsndiscord.utils.Utilities;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Member;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
	
	@Override
	public boolean isAllowed(final Member member){
		return Objects.nonNull(member) && (Utilities.isTeam(member) || Settings.get(member.getGuild()).getDjRole().map(RoleConfiguration::getRole).filter(Optional::isPresent).map(Optional::get).map(role -> member.getRoles().contains(role)).orElse(false));
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
