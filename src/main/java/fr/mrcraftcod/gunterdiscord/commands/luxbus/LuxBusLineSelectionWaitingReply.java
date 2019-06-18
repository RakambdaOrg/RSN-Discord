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
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by mrcraftcod (MrCraftCod - zerderr@gmail.com) on 2019-05-18.
 *
 * @author Thomas Couchoud
 * @since 2019-05-18
 */
public class LuxBusLineSelectionWaitingReply implements WaitingUserReply{
	private final List<LuxBusDeparture> departures;
	private final long infoMessageId;
	private final long infoTextChannelId;
	private final GuildMessageReceivedEvent event;
	private boolean handled;
	
	public LuxBusLineSelectionWaitingReply(@NotNull final GuildMessageReceivedEvent event, @NotNull final List<LuxBusDeparture> departures, final long infoTextChannelId, final long infoMessageId){
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
	public boolean execute(@NotNull final GuildMessageReceivedEvent event, @NotNull final LinkedList<String> args){
		if(args.isEmpty()){
			Actions.reply(event, "Invalid selection");
		}
		else{
			final var line = args.pop();
			final var filtered = this.departures.stream().filter(departure -> Objects.equals(departure.getProduct().getLine(), line)).collect(Collectors.toList());
			if(filtered.isEmpty()){
				Actions.reply(event, "Invalid selection");
			}
			else{
				Actions.deleteMessageById(this.infoTextChannelId, this.infoMessageId);
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
