package fr.mrcraftcod.gunterdiscord.commands.luxbus;

import fr.mrcraftcod.gunterdiscord.commands.luxbus.utils.LuxBusDeparture;
import fr.mrcraftcod.gunterdiscord.listeners.reply.ReplyMessageListener;
import fr.mrcraftcod.gunterdiscord.listeners.reply.WaitingUserReply;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by mrcraftcod (MrCraftCod - zerderr@gmail.com) on 2019-05-18.
 *
 * @author Thomas Couchoud
 * @since 2019-05-18
 */
public class LuxBusDirectionSelectionWaitingReply implements WaitingUserReply{
	private final long infoMessageId;
	private final long infoTextChannelId;
	private final Map<Integer, List<LuxBusDeparture>> departures;
	private final GuildMessageReceivedEvent event;
	private boolean handled;
	
	public LuxBusDirectionSelectionWaitingReply(@NotNull final GuildMessageReceivedEvent event, @NotNull final Map<Integer, List<LuxBusDeparture>> departures, final long infoTextChannelId, final long infoMessageId){
		this.event = event;
		this.handled = false;
		this.departures = departures;
		this.infoMessageId = infoMessageId;
		this.infoTextChannelId = infoTextChannelId;
		ReplyMessageListener.getExecutor().schedule(() -> {
			if(!this.isHandled()){
				this.onExpire();
			}
		}, 30, TimeUnit.SECONDS);
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
					Actions.deleteMessageById(this.infoTextChannelId, this.infoMessageId);
					Actions.deleteMessage(event.getMessage());
					this.handled = true;
					this.departures.get(direction).stream().sorted().forEachOrdered(departure -> Actions.reply(event, departure.getAsEmbed(new EmbedBuilder()).build()));
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
		Actions.deleteMessageById(this.infoTextChannelId, this.infoMessageId);
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
