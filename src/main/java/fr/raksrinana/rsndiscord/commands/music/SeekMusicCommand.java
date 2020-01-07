package fr.raksrinana.rsndiscord.commands.music;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.log.Log;
import fr.raksrinana.rsndiscord.utils.player.RSNAudioManager;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class SeekMusicCommand extends BasicCommand{
	private static final Pattern TIME_PATTERN = Pattern.compile("((\\d{1,2}):)?((\\d{1,2}):)?(\\d{1,2})");
	private static final int SECOND_PER_MINUTE = 60;
	private static final int SECOND_PER_HOUR = 3600;
	
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	SeekMusicCommand(@NonNull final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("Time", "The time to seek, must be in the format hh:mm:ss ou mm:ss or ss", false);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		if(args.isEmpty()){
			return CommandResult.BAD_ARGUMENTS;
		}
		else{
			final var time = this.parseTime(event.getGuild(), args.pop());
			if(time < 0){
				Actions.reply(event, "Invalid format", null);
			}
			else{
				switch(RSNAudioManager.seek(event.getGuild(), time)){
					case NO_MUSIC:
						Actions.reply(event, MessageFormat.format("{0}, No music currently playing", event.getAuthor().getAsMention()), null);
						break;
					case OK:
						Actions.reply(event, MessageFormat.format("{0} seeked the music to {1}", event.getAuthor().getAsMention(), NowPlayingMusicCommand.getDuration(time)), null);
						break;
					case IMPOSSIBLE:
						Actions.reply(event, MessageFormat.format("{0}, the time of this music cannot be changed", event.getAuthor().getAsMention()), null);
						break;
				}
			}
		}
		return CommandResult.SUCCESS;
	}
	
	private long parseTime(@NonNull final Guild guild, @NonNull final String time){
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
	
	@NonNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + "<time>";
	}
	
	@Override
	public boolean isAllowed(final Member member){
		return Objects.nonNull(member) && (Utilities.isTeam(member) || RSNAudioManager.isRequester(member.getGuild(), member.getUser()));
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Seek";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("seek");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Seek a time into the music";
	}
}
