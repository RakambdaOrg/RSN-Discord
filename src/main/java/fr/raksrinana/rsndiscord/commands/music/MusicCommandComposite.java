package fr.raksrinana.rsndiscord.commands.music;

import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import fr.raksrinana.rsndiscord.utils.Utilities;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-08-18
 */
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
	public boolean isAllowed(@Nullable final Member member){
		return Objects.nonNull(member) && (Utilities.isTeam(member) || Settings.getConfiguration(member.getGuild()).getDjRole().map(RoleConfiguration::getRole).filter(Optional::isPresent).map(Optional::get).map(role -> Utilities.hasRole(member, role)).orElse(false));
	}
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Music";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("music", "m");
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return "Handles music interactions";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
