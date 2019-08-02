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
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
	private static final int SECOND_PER_MINUTE = 60;
	private static final int SECOND_PER_HOUR = 3600;
	
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	SeekMusicCommand(@Nonnull final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@Nonnull final Guild guild, @Nonnull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("Time", "The time to seek, must be in the format hh:mm:ss ou mm:ss or ss", false);
	}
	
	@Nonnull
	@Override
	public CommandResult execute(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		if(args.isEmpty()){
			Actions.reply(event, "Please give the time to seek");
		}
		else{
			final var time = this.parseTime(event.getGuild(), args.pop());
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
	
	private long parseTime(@Nonnull final Guild guild, @Nonnull final String time){
		final var matcher = TIME_PATTERN.matcher(time);
		if(!matcher.matches()){
			return -1;
		}
		var duration = 0L;
		duration += this.getAsInt(guild, matcher.group(2)) * SECOND_PER_HOUR;
		duration += this.getAsInt(guild, matcher.group(4)) * SECOND_PER_MINUTE;
		duration += this.getAsInt(guild, matcher.group(5));
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
	
	@Nonnull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + "<time>";
	}
	
	@Override
	public boolean isAllowed(@Nullable final Member member){
		return Objects.nonNull(member) && (Utilities.isTeam(member) || GunterAudioManager.isRequester(member.getGuild(), member.getUser()));
	}
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Seek";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("seek");
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return "Seek a time into the music";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
