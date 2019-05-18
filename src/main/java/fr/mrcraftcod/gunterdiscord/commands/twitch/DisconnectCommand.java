package fr.mrcraftcod.gunterdiscord.commands.twitch;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.irc.TwitchIRC;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-06-16
 */
public class DisconnectCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	DisconnectCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NotNull final Guild guild, @NotNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("user", "The twitch user", false);
	}
	
	@Override
	public CommandResult execute(final GuildMessageReceivedEvent event, @NotNull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		if(args.isEmpty()){
			Actions.reply(event, "Please give a twitch user");
		}
		else{
			TwitchIRC.disconnect(event.getGuild(), args.poll());
		}
		return CommandResult.SUCCESS;
	}
	
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <user>";
	}
	
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ADMIN;
	}
	
	@Override
	public String getName(){
		return "Disconnect";
	}
	
	@Override
	public List<String> getCommand(){
		return List.of("disconnect", "d", "l");
	}
	
	@Override
	public String getDescription(){
		return "Leaves a twitch chat";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
