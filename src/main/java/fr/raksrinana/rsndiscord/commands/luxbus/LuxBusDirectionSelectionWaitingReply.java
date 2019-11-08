package fr.raksrinana.rsndiscord.commands.luxbus;

import fr.raksrinana.rsndiscord.listeners.reply.BasicWaitingUserReply;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.luxbus.LuxBusDeparture;
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
	public boolean onExecute(@Nonnull final GuildMessageReactionAddEvent event){
		return false;
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
	public long getEmoteMessageId(){
		return -1;
	}
}
