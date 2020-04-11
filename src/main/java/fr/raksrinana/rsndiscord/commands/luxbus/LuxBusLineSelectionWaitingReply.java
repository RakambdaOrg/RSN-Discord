package fr.raksrinana.rsndiscord.commands.luxbus;

import fr.raksrinana.rsndiscord.listeners.reply.BasicWaitingUserReply;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.luxbus.LuxBusDeparture;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class LuxBusLineSelectionWaitingReply extends BasicWaitingUserReply{
	private final Collection<LuxBusDeparture> departures;
	
	LuxBusLineSelectionWaitingReply(@NonNull final GuildMessageReceivedEvent event, @NonNull final Set<LuxBusDeparture> departures, @NonNull final Message infoMessage){
		super(event, event.getAuthor(), infoMessage);
		this.departures = departures;
	}
	
	@Override
	public boolean onExecute(@NonNull final GuildMessageReactionAddEvent event){
		return false;
	}
	
	@Override
	public long getEmoteMessageId(){
		return -1;
	}
	
	@Override
	protected boolean onExecute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		if(args.isEmpty()){
			Actions.reply(event, "Invalid selection", null);
		}
		else{
			final var line = args.pop();
			final var filtered = this.departures.stream().filter(departure -> Objects.equals(departure.getProduct().getLine(), line)).collect(Collectors.toSet());
			if(filtered.isEmpty()){
				Actions.reply(event, "Invalid selection", null);
			}
			else{
				Actions.deleteMessage(event.getMessage());
				if(filtered.stream().map(LuxBusDeparture::getDirection).distinct().count() < 2){
					filtered.stream().sorted().forEachOrdered(departure -> Actions.reply(event, "", departure.getAsEmbed(new EmbedBuilder()).build()));
				}
				else{
					LuxBusGetStopCommand.askDirection(event, filtered);
				}
				return true;
			}
		}
		return false;
	}
}
