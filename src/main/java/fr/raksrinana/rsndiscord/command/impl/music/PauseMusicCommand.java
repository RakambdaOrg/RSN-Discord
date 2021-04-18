package fr.raksrinana.rsndiscord.command.impl.music;

import fr.raksrinana.rsndiscord.command.BasicCommand;
import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.music.RSNAudioManager;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.schedule.ScheduleUtils.deleteMessage;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

public class PauseMusicCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	PauseMusicCommand(@NotNull Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NotNull Guild guild, @NotNull EmbedBuilder builder){
		super.addHelp(guild, builder);
	}
	
	@NotNull
	@Override
	public CommandResult execute(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
		super.execute(event, args);
		var guild = event.getGuild();
		
		var message = switch(RSNAudioManager.pause(guild)){
			case NO_MUSIC -> "music.nothing-playing";
			case OK -> "music.paused";
			case IMPOSSIBLE -> "unknown";
		};
		JDAWrappers.message(event, translate(guild, message, event.getAuthor().getAsMention())).submit()
				.thenAccept(deleteMessage(date -> date.plusMinutes(5)));
		return SUCCESS;
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return new SimplePermission("command.music.pause", false);
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return translate(guild, "command.music.pause.name");
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return translate(guild, "command.music.pause.description");
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("pause", "p");
	}
}
