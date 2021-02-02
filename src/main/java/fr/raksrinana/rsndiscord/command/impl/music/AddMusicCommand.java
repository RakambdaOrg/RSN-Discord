package fr.raksrinana.rsndiscord.command.impl.music;

import fr.raksrinana.rsndiscord.command.BasicCommand;
import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.music.RSNAudioManager;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.command.CommandResult.BAD_ARGUMENTS;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.schedule.ScheduleUtils.deleteMessage;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

public class AddMusicCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	AddMusicCommand(Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NotNull Guild guild, @NotNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("link", translate(guild, "command.music.add.help.link"), false)
				.addField("skip", translate(guild, "command.music.add.help.skip"), false)
				.addField("max", translate(guild, "command.music.add.help.max"), false)
				.addField("repeat", translate(guild, "command.music.add.help.repeat"), false);
	}
	
	@NotNull
	@Override
	public CommandResult execute(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
		super.execute(event, args);
		if(args.isEmpty()){
			return BAD_ARGUMENTS;
		}
		
		var guild = event.getGuild();
		var author = event.getAuthor();
		var channel = event.getChannel();
		var voiceChannel = ofNullable(event.getMember())
				.map(Member::getVoiceState)
				.map(GuildVoiceState::getChannel);
		
		if(voiceChannel.isEmpty()){
			channel.sendMessage(translate(guild, "music.voice-error")).submit()
					.thenAccept(deleteMessage(date -> date.plusMinutes(5)));
			return SUCCESS;
		}
		
		var identifier = requireNonNull(args.poll()).trim();
		var skipCount = getArgumentAsInteger(args)
				.filter(value -> value >= 0)
				.orElse(0);
		var maxTracks = getArgumentAsInteger(args)
				.filter(value -> value >= 0)
				.orElse(10);
		var repeat = ofNullable(args.poll())
				.map(Boolean::valueOf)
				.orElse(false);
		
		var trackConsumer = new AddMusicTrackConsumer(guild, channel, author, repeat);
		RSNAudioManager.play(author, voiceChannel.get(), trackConsumer, skipCount, maxTracks, identifier);
		return SUCCESS;
	}
	
	@NotNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <link> [skip] [max] [repeat]";
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return new SimplePermission("command.music.add", false);
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return translate(guild, "command.music.add.name");
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return translate(guild, "command.music.add.description");
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("add", "a");
	}
}
