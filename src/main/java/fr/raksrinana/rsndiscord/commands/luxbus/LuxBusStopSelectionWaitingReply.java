package fr.raksrinana.rsndiscord.commands.luxbus;

import fr.raksrinana.rsndiscord.listeners.reply.BasicWaitingUserReply;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.luxbus.LuxBusStop;
import lombok.NonNull;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import java.util.LinkedList;
import java.util.List;

public class LuxBusStopSelectionWaitingReply extends BasicWaitingUserReply{
	private final List<LuxBusStop> stops;
	
	LuxBusStopSelectionWaitingReply(@NonNull final GuildMessageReceivedEvent event, @NonNull final List<LuxBusStop> stops){
		super(event, event.getAuthor());
		this.stops = stops;
	}
	
	@Override
	public boolean onExecute(@NonNull final GuildMessageReactionAddEvent event){
		return false;
	}
	
	@Override
	protected boolean onExecute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		if(args.isEmpty()){
			Actions.reply(event, "Invalid selection", null);
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
					Actions.reply(event, "Invalid selection", null);
				}
			}
			catch(final NumberFormatException e){
				Actions.reply(event, "Please enter the corresponding number", null);
			}
		}
		return false;
	}
	
	@Override
	public long getEmoteMessageId(){
		return -1;
	}
}
