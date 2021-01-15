package fr.raksrinana.rsndiscord.command.impl.music;

import fr.raksrinana.rsndiscord.command.BasicCommand;
import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.music.RSNAudioManager;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import fr.raksrinana.rsndiscord.utils.Utilities;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import static fr.raksrinana.rsndiscord.command.CommandResult.BAD_ARGUMENTS;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.schedule.ScheduleUtils.deleteMessage;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.util.Objects.isNull;

public class SeekMusicCommand extends BasicCommand{
	private static final Pattern TIME_PATTERN = Pattern.compile("((\\d{1,2}):)?((\\d{1,2}):)?(\\d{1,2})");
	private static final long SECOND_PER_MINUTE = 60;
	private static final long SECOND_PER_HOUR = 3600;
	
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	SeekMusicCommand(@NonNull final Command parent){
		super(parent);
	}
	
	@Override
	public boolean isAllowed(final @NonNull Member member){
		return RSNAudioManager.isRequester(member.getGuild(), member.getUser()) || Utilities.isModerator(member);
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return new SimplePermission("command.music.seek", false);
	}
	
	private static long getAsLong(final Guild guild, final String str){
		if(isNull(str) || str.isBlank()){
			return 0;
		}
		try{
			return Long.parseLong(str);
		}
		catch(final Exception e){
			Log.getLogger(guild).error("Error parsing {} into long", str, e);
		}
		return 0;
	}
	
	private static long parseTime(@NonNull final Guild guild, @NonNull final String time){
		final var matcher = TIME_PATTERN.matcher(time);
		if(!matcher.matches()){
			return -1;
		}
		var duration = 0L;
		duration += SeekMusicCommand.getAsLong(guild, matcher.group(2)) * SECOND_PER_HOUR;
		duration += SeekMusicCommand.getAsLong(guild, matcher.group(4)) * SECOND_PER_MINUTE;
		duration += SeekMusicCommand.getAsLong(guild, matcher.group(5));
		return duration * 1000;
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		if(args.isEmpty()){
			return BAD_ARGUMENTS;
		}
		
		var guild = event.getGuild();
		var channel = event.getChannel();
		var time = SeekMusicCommand.parseTime(guild, args.pop());
		
		if(time < 0){
			channel.sendMessage(translate(guild, "music.invalid-format")).submit()
					.thenAccept(deleteMessage(date -> date.plusMinutes(5)));
		}
		else{
			var message = switch(RSNAudioManager.seek(guild, time)){
				case NO_MUSIC -> "music.nothing-playing";
				case OK -> "music.seeked";
				case IMPOSSIBLE -> "music.seek-error";
			};
			channel.sendMessage(translate(guild, message, event.getAuthor().getAsMention())).submit()
					.thenAccept(deleteMessage(date -> date.plusMinutes(5)));
		}
		return SUCCESS;
	}
	
	@NonNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + "<time>";
	}
	
	@Override
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("Time", translate(guild, "command.music.seek.help.time"), false);
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.music.seek.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("seek");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.music.seek.description");
	}
}
