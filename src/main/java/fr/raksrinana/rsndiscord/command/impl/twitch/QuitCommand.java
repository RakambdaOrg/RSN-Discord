package fr.raksrinana.rsndiscord.command.impl.twitch;

import fr.raksrinana.rsndiscord.api.irc.TwitchUtils;
import fr.raksrinana.rsndiscord.command.BasicCommand;
import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

public class QuitCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	QuitCommand(Command parent){
		super(parent);
	}
	
	@NotNull
	@Override
	public CommandResult execute(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
		super.execute(event, args);
		TwitchUtils.disconnectAll();
		return SUCCESS;
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return new SimplePermission("command.twitch.quit", false);
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return translate(guild, "command.twitch.quit.name");
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return translate(guild, "command.twitch.quit.description");
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("quit", "q");
	}
}
