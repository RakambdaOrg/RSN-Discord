package fr.raksrinana.rsndiscord.commands.luxbus;

import fr.raksrinana.rsndiscord.listeners.reply.BasicWaitingUserReply;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.luxbus.LuxBusDeparture;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LuxBusDirectionSelectionWaitingReply extends BasicWaitingUserReply{
	private final Map<Integer, List<LuxBusDeparture>> departures;
	
	LuxBusDirectionSelectionWaitingReply(@NonNull final GuildMessageReceivedEvent event, @NonNull final Map<Integer, List<LuxBusDeparture>> departures, @NonNull final Message infoMessage){
		super(event, event.getAuthor(), infoMessage);
		this.departures = departures;
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
				final var direction = Integer.parseInt(args.pop());
				if(this.departures.containsKey(direction)){
					Actions.deleteMessage(event.getMessage());
					this.departures.get(direction).stream().sorted().forEachOrdered(departure -> Actions.reply(event, "", departure.getAsEmbed(Utilities.buildEmbed(event.getAuthor(), null, null, null)).build()));
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
		return this.isHandled();
	}
	
	@Override
	public long getEmoteMessageId(){
		return -1;
	}
}
