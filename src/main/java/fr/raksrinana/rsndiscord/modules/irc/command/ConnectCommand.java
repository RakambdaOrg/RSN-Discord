package fr.raksrinana.rsndiscord.modules.irc.command;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.modules.irc.twitch.TwitchIRC;
import fr.raksrinana.rsndiscord.modules.permission.IPermission;
import fr.raksrinana.rsndiscord.modules.permission.SimplePermission;
import fr.raksrinana.rsndiscord.utils.Actions;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

public class ConnectCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	ConnectCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("user", translate(guild, "command.twitch.connect.help.user"), false);
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return new SimplePermission("command.twitch.connect", false);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		if(args.isEmpty()){
			return CommandResult.BAD_ARGUMENTS;
		}
		try{
			TwitchIRC.connect(event.getGuild(), args.pop());
		}
		catch(final NoSuchElementException | IOException e){
			Actions.reply(event, translate(event.getGuild(), "twitch.not-configured"), null);
			Log.getLogger(event.getGuild()).warn("Missing configuration for IRC", e);
		}
		return CommandResult.SUCCESS;
	}
	
	@NonNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <user>";
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.twitch.connect.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("connect", "c", "j");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.twitch.connect.description");
	}
}