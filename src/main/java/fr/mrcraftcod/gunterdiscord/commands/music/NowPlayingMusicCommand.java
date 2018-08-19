package fr.mrcraftcod.gunterdiscord.commands.music;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import fr.mrcraftcod.gunterdiscord.utils.player.GunterAudioManager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.awt.*;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-06-16
 */
public class NowPlayingMusicCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	public NowPlayingMusicCommand(Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NotNull Guild guild, @NotNull EmbedBuilder builder){
		super.addHelp(guild, builder);
	}
	
	@Override
	public CommandResult execute(@NotNull MessageReceivedEvent event, @NotNull LinkedList<String> args) throws Exception{
		super.execute(event, args);
		var builder = Utilities.buildEmbed(event.getAuthor(), Color.CYAN, "En cours de diffusion");
		GunterAudioManager.currentTrack(event.getGuild()).ifPresentOrElse(track -> {
			builder.addField("Titre", track.getInfo().title, false);
			builder.addField("Position", String.format("%s %s / %s", buildBar(track.getPosition(), track.getDuration()), getDuration(track.getPosition()), getDuration(track.getDuration())), false);
		}, () -> {
			builder.setColor(Color.RED);
			builder.setDescription("Aucune musique ne joue actuellement");
		});
		
		Actions.reply(event, builder.build());
		return CommandResult.SUCCESS;
	}
	
	private String buildBar(double current, double total){
		var charCount = 10;
		if(current > total){
			current = total;
		}
		StringBuilder sb = new StringBuilder();
		IntStream.range(0, charCount).forEach(i -> sb.append('='));
		int replaceIndex = (int) ((current / total) * charCount);
		sb.replace(replaceIndex, replaceIndex + 1, "O");
		return sb.toString();
	}
	
	/**
	 * Get the duration in a readable format.
	 *
	 * @param time The duration in milliseconds.
	 *
	 * @return A readable version of this duration.
	 */
	private static String getDuration(long time){
		var duration = Duration.ofMillis(time);
		if(duration.toHoursPart() > 0){
			return String.format("%02d:%02d:%02d", duration.toHoursPart(), duration.toMinutesPart(), duration.toSecondsPart());
		}
		return String.format("%02d:%02d", duration.toMinutesPart(), duration.toSecondsPart());
	}
	
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage();
	}
	
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@Override
	public String getName(){
		return "En cours musique";
	}
	
	@Override
	public List<String> getCommand(){
		return List.of("nowplaying", "np");
	}
	
	@Override
	public String getDescription(){
		return "Obtient des infos sur la musique en cours";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
