package fr.mrcraftcod.gunterdiscord.commands.luxbus;

import fr.mrcraftcod.gunterdiscord.listeners.reply.BasicWaitingUserReply;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import fr.mrcraftcod.gunterdiscord.utils.luxbus.LuxBusDeparture;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by mrcraftcod (MrCraftCod - zerderr@gmail.com) on 2019-05-18.
 *
 * @author Thomas Couchoud
 * @since 2019-05-18
 */
public class LuxBusDirectionSelectionWaitingReply extends BasicWaitingUserReply{
	private final Map<Integer, List<LuxBusDeparture>> departures;
	
	LuxBusDirectionSelectionWaitingReply(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final Map<Integer, List<LuxBusDeparture>> departures, @Nonnull final Message infoMessage){
		super(event, event.getAuthor(), infoMessage);
		this.departures = departures;
	}
	
	@Override
	protected boolean onExecute(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args){
		if(args.isEmpty()){
			Actions.reply(event, "Invalid selection");
		}
		else{
			try{
				final var direction = Integer.parseInt(args.pop());
				if(this.departures.containsKey(direction)){
					Actions.deleteMessage(event.getMessage());
					this.departures.get(direction).stream().sorted().forEachOrdered(departure -> Actions.reply(event, departure.getAsEmbed(Utilities.buildEmbed(event.getAuthor(), null, null)).build()));
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
		return this.isHandled();
	}
	
	@Override
	public boolean onExecute(@Nonnull GuildMessageReactionAddEvent event){
		return false;
	}
	
	@Override
	public long getEmoteMessageId(){
		return -1;
	}
}
