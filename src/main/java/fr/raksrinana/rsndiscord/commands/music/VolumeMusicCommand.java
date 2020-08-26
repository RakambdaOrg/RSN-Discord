package fr.raksrinana.rsndiscord.commands.music;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.music.RSNAudioManager;
import fr.raksrinana.rsndiscord.utils.permission.Permission;
import fr.raksrinana.rsndiscord.utils.permission.SimplePermission;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

public class VolumeMusicCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	VolumeMusicCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("Volume", translate(guild, "command.music.volume.help.volume"), false);
	}
	
	@Override
	public @NonNull Permission getPermission(){
		return new SimplePermission("command.music.volume", false);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		if(args.isEmpty()){
			return CommandResult.BAD_ARGUMENTS;
		}
		else{
			try{
				final var volume = Math.min(100, Math.max(0, Integer.parseInt(args.pop())));
				Settings.get(event.getGuild()).setMusicVolume(volume);
				RSNAudioManager.getFor(event.getGuild()).ifPresent(g -> g.setVolume(volume));
				Actions.reply(event, translate(event.getGuild(), "music.volume-set", volume), null);
			}
			catch(NumberFormatException e){
				Actions.reply(event, translate(event.getGuild(), "music.invalid-format"), null);
			}
		}
		return CommandResult.SUCCESS;
	}
	
	@NonNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <volume>";
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.music.volume.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("volume");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.music.volume.description");
	}
}
