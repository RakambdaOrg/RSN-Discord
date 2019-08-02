package fr.mrcraftcod.gunterdiscord.commands.luxbus;

import fr.mrcraftcod.gunterdiscord.listeners.reply.BasicWaitingUserReply;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.luxbus.LuxBusStop;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by mrcraftcod (MrCraftCod - zerderr@gmail.com) on 2019-05-18.
 *
 * @author Thomas Couchoud
 * @since 2019-05-18
 */
public class LuxBusStopSelectionWaitingReply extends BasicWaitingUserReply{
	private final List<LuxBusStop> stops;
	
	LuxBusStopSelectionWaitingReply(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final List<LuxBusStop> stops){
		super(event, event.getAuthor());
		this.stops = stops;
	}
	
	@Override
	protected boolean onExecute(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args){
		if(args.isEmpty()){
			Actions.reply(event, "Invalid selection");
		}
		else{
			try{
				final var stop = Integer.parseInt(args.pop());
				if(stop > 0 && stop <= this.stops.size()){
					Actions.deleteMessage(event.getMessage());
					LuxBusGetStopCommand.askLine(event, this.stops.get(stop - 1));
					return true;
				}
				else{
					Actions.reply(event, "Invalid selection");
				}
			}
			catch(final NumberFormatException e){
				Actions.reply(event, "Please enter the corresponding number");
			}
		}
		return false;
	}
	
	@Override
	public boolean onExecute(@Nonnull final GuildMessageReactionAddEvent event){
		return false;
	}
	
	@Override
	public long getEmoteMessageId(){
		return -1;
	}
}
