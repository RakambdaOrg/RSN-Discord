package fr.mrcraftcod.gunterdiscord.commands.music;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import fr.mrcraftcod.gunterdiscord.utils.log.Log;
import fr.mrcraftcod.gunterdiscord.utils.player.GunterAudioManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-06-16
 */
public class SeekMusicCommand extends BasicCommand{
	private static final Pattern TIME_PATTERN = Pattern.compile("((\\d{1,2}):)?((\\d{1,2}):)?(\\d{1,2})");
	
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	SeekMusicCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NotNull final Guild guild, @NotNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("Time", "The time to seek, must be in the format hh:mm:ss ou mm:ss or ss", false);
	}
	
	@Override
	public CommandResult execute(@NotNull final MessageReceivedEvent event, @NotNull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		if(args.isEmpty()){
			Actions.reply(event, "Please give the time to seek");
		}
		else{
			final var time = parseTime(event.getGuild(), args.poll());
			if(time < 0){
				Actions.reply(event, "Invalid format");
			}
			else{
				switch(GunterAudioManager.seek(event.getGuild(), time)){
					case NO_MUSIC:
						Actions.reply(event, "%s, No music currently playing", event.getAuthor().getAsMention());
						break;
					case OK:
						Actions.reply(event, "%s seeked the music to %s", event.getAuthor().getAsMention(), NowPlayingMusicCommand.getDuration(time));
						break;
					case IMPOSSIBLE:
						Actions.reply(event, "%s, the time of this music cannot be changed", event.getAuthor().getAsMention());
						break;
				}
			}
		}
		return CommandResult.SUCCESS;
	}
	
	private long parseTime(final Guild guild, final String time){
		final var matcher = TIME_PATTERN.matcher(time);
		if(!matcher.matches()){
			return -1;
		}
		var duration = 0L;
		duration += getAsInt(guild, matcher.group(2)) * 3600;
		duration += getAsInt(guild, matcher.group(4)) * 60;
		duration += getAsInt(guild, matcher.group(5));
		return duration * 1000;
	}
	
	private int getAsInt(final Guild guild, final String str){
		if(Objects.isNull(str) || str.isBlank()){
			return 0;
		}
		try{
			return Integer.parseInt(str);
		}
		catch(final Exception e){
			Log.getLogger(guild).error("Error paring {} into int", str, e);
		}
		return 0;
	}
	
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + "<time>";
	}
	
	@Override
	public boolean isAllowed(final Member member){
		return Utilities.isTeam(member) || GunterAudioManager.isRequester(member.getGuild(), member.getUser());
	}
	
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@Override
	public String getName(){
		return "Seek";
	}
	
	@Override
	public List<String> getCommand(){
		return List.of("seek");
	}
	
	@Override
	public String getDescription(){
		return "Seek a time into the music";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
