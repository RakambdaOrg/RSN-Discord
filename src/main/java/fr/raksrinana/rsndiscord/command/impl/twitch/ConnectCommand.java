package fr.raksrinana.rsndiscord.command.impl.twitch;

import fr.raksrinana.rsndiscord.api.irc.TwitchUtils;
import fr.raksrinana.rsndiscord.command.BasicCommand;
import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.command.CommandResult.BAD_ARGUMENTS;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.schedule.ScheduleUtils.deleteMessage;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

public class ConnectCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	ConnectCommand(Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NotNull Guild guild, @NotNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("user", translate(guild, "command.twitch.connect.help.user"), false);
	}
	
	@NotNull
	@Override
	public CommandResult execute(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
		super.execute(event, args);
		if(args.isEmpty()){
			return BAD_ARGUMENTS;
		}
		try{
			TwitchUtils.connect(event.getGuild(), args.pop());
		}
		catch(Exception e){
			JDAWrappers.message(event, translate(event.getGuild(), "twitch.not-configured")).submit()
					.thenAccept(deleteMessage(date -> date.plusMinutes(5)));
			Log.getLogger(event.getGuild()).warn("Missing configuration for IRC", e);
		}
		return SUCCESS;
	}
	
	@NotNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <user>";
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return new SimplePermission("command.twitch.connect", false);
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return translate(guild, "command.twitch.connect.name");
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return translate(guild, "command.twitch.connect.description");
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("connect", "c", "j");
	}
}
