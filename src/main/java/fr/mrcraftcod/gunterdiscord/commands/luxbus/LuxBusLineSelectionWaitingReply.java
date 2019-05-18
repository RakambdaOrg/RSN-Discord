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
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by mrcraftcod (MrCraftCod - zerderr@gmail.com) on 2019-05-18.
 *
 * @author Thomas Couchoud
 * @since 2019-05-18
 */
public class LuxBusLineSelectionWaitingReply implements WaitingUserReply{
	private final long maxTime;
	private final List<LuxBusDeparture> departures;
	private final Message infoMessage;
	private final GuildMessageReceivedEvent event;
	private boolean handled;
	
	public LuxBusLineSelectionWaitingReply(GuildMessageReceivedEvent event, List<LuxBusDeparture> departures, Message infoMessage){
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
	public User getUser(){
		return event.getAuthor();
	}
	
	@Override
	public TextChannel getChannel(){
		return event.getChannel();
	}
	
	@Override
	public boolean execute(GuildMessageReceivedEvent event, LinkedList<String> args){
		if(args.isEmpty()){
			Actions.reply(event, "Invalid selection");
		}
		else{
			final var line = args.pop();
			final var filtered = this.departures.stream().filter(d -> Objects.equals(d.getProduct().getLine(), line)).collect(Collectors.toList());
			if(filtered.isEmpty()){
				Actions.reply(event, "Invalid selection");
			}
			else{
				Actions.deleteMessage(infoMessage);
				Actions.deleteMessage(event.getMessage());
				this.handled = true;
				if(filtered.stream().map(LuxBusDeparture::getDirection).distinct().count() < 2){
					filtered.stream().sorted().forEachOrdered(d -> Actions.reply(event, d.getAsEmbed(new EmbedBuilder()).build()));
				}
				else{
					LuxBusGetStopCommand.askDirection(event, filtered);
				}
			}
		}
		return this.isHandled();
	}
	
	@Override
	public boolean onExpire(){
		Actions.reply(event, "%s you didn't reply in time", this.getUser().getAsMention());
		Actions.deleteMessage(infoMessage);
		this.handled = true;
		return this.isHandled();
	}
}
