package fr.raksrinana.rsndiscord.commands.twitch;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.irc.TwitchIRC;
import fr.raksrinana.rsndiscord.utils.log.Log;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-06-16
 */
public class ConnectCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	ConnectCommand(@Nullable final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@Nonnull final Guild guild, @Nonnull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("user", "The twitch user", false);
	}
	
	@Nonnull
	@Override
	public CommandResult execute(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		if(args.isEmpty()){
			Actions.reply(event, "Please give a twitch user");
		}
		else{
			try{
				final var list = TwitchIRC.getConnectedTo();
				if(list.isEmpty()){
					TwitchIRC.connect(event.getGuild(), args.pop());
				}
				else{
					Actions.replyFormatted(event, "The bot is already connected to %s", String.join(", ", list));
				}
			}
			catch(final NoSuchElementException e){
				Actions.reply(event, "Server needs to be configured to use this feature");
				Log.getLogger(event.getGuild()).warn("Missing configuration for IRC", e);
			}
		}
		return CommandResult.SUCCESS;
	}
	
	@Nonnull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <user>";
	}
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ADMIN;
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Connect";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("connect", "c", "j");
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return "Joins a twitch chat";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
