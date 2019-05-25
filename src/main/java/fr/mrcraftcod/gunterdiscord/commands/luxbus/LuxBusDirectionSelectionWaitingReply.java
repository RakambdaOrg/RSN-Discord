package fr.mrcraftcod.gunterdiscord.commands.luxbus;

import fr.mrcraftcod.gunterdiscord.commands.luxbus.utils.LuxBusDeparture;
import fr.mrcraftcod.gunterdiscord.listeners.reply.WaitingUserReply;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by mrcraftcod (MrCraftCod - zerderr@gmail.com) on 2019-05-18.
 *
 * @author Thomas Couchoud
 * @since 2019-05-18
 */
public class LuxBusDirectionSelectionWaitingReply implements WaitingUserReply{
	private final long maxTime;
	private final Message infoMessage;
	private final Map<Integer, List<LuxBusDeparture>> departures;
	private final GuildMessageReceivedEvent event;
	private boolean handled;
	
	public LuxBusDirectionSelectionWaitingReply(final GuildMessageReceivedEvent event, final Map<Integer, List<LuxBusDeparture>> departures, final Message infoMessage){
		this.event = event;
		this.handled = false;
		this.maxTime = System.currentTimeMillis() + 30000;
		this.departures = departures;
		this.infoMessage = infoMessage;
	}
	
	@Override
	public boolean isExpired(){
		return System.currentTimeMillis() > this.maxTime;
	}
	
	@Override
	public boolean isHandled(){
		return this.handled;
	}
	
	@Override
	public boolean execute(final GuildMessageReceivedEvent event, final LinkedList<String> args){
		if(args.isEmpty()){
			Actions.reply(event, "Invalid selection");
		}
		else{
			try{
				final var direction = Integer.parseInt(args.pop());
				if(this.departures.containsKey(direction)){
					Actions.deleteMessage(this.infoMessage);
					Actions.deleteMessage(event.getMessage());
					this.handled = true;
					this.departures.get(direction).stream().sorted().forEachOrdered(d -> Actions.reply(event, d.getAsEmbed(new EmbedBuilder()).build()));
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
	public boolean onExpire(){
		Actions.reply(this.event, "%s you didn't reply in time", this.getUser().getAsMention());
		Actions.deleteMessage(this.infoMessage);
		this.handled = true;
		return this.isHandled();
	}
	
	@Override
	public TextChannel getChannel(){
		return this.event.getChannel();
	}
	
	@Override
	public User getUser(){
		return this.event.getAuthor();
	}
}
