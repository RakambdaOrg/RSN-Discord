package fr.mrcraftcod.gunterdiscord.commands.luxbus;

import fr.mrcraftcod.gunterdiscord.commands.luxbus.utils.LuxBusStop;
import fr.mrcraftcod.gunterdiscord.listeners.reply.ReplyMessageListener;
import fr.mrcraftcod.gunterdiscord.listeners.reply.WaitingUserReply;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by mrcraftcod (MrCraftCod - zerderr@gmail.com) on 2019-05-18.
 *
 * @author Thomas Couchoud
 * @since 2019-05-18
 */
public class LuxBusStopSelectionWaitingReply implements WaitingUserReply{
	private final List<Pair<Long, Long>> infoMessages;
	private final List<LuxBusStop> stops;
	private final GuildMessageReceivedEvent event;
	private final long infoTextChannelId;
	private boolean handled;
	
	public LuxBusStopSelectionWaitingReply(final GuildMessageReceivedEvent event, final List<LuxBusStop> stops, final long infoTextChannelId){
		this.event = event;
		this.handled = false;
		this.stops = stops;
		this.infoMessages = new ArrayList<>();
		this.infoTextChannelId = infoTextChannelId;
		ReplyMessageListener.getExecutor().schedule(() -> {
			if(!isHandled()){
				this.onExpire();
			}
		}, 30, TimeUnit.SECONDS);
	}
	
	public void addMessage(final Message message){
		this.infoMessages.add(ImmutablePair.of(message.getTextChannel().getIdLong(), message.getIdLong()));
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
				final var stop = Integer.parseInt(args.pop());
				if(stop > 0 && stop <= this.stops.size()){
					this.infoMessages.forEach(pair -> Actions.deleteMessageById(pair.getLeft(), pair.getRight()));
					Actions.deleteMessage(event.getMessage());
					this.handled = true;
					LuxBusGetStopCommand.askLine(event, this.stops.get(stop - 1));
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
		this.infoMessages.forEach(pair -> Actions.deleteMessageById(pair.getLeft(), pair.getRight()));
		this.handled = true;
		return this.isHandled();
	}
	
	@Override
	public long getChannel(){
		return this.infoTextChannelId;
	}
	
	@Override
	public User getUser(){
		return this.event.getAuthor();
	}
}
