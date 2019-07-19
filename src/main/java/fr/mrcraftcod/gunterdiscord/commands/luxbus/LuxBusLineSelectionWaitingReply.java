package fr.mrcraftcod.gunterdiscord.commands.luxbus;

import fr.mrcraftcod.gunterdiscord.listeners.reply.BasicWaitingUserReply;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.luxbus.LuxBusDeparture;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import javax.annotation.Nonnull;
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
public class LuxBusLineSelectionWaitingReply extends BasicWaitingUserReply{
	private final List<LuxBusDeparture> departures;
	
	LuxBusLineSelectionWaitingReply(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final List<LuxBusDeparture> departures, @Nonnull final Message infoMessage){
		super(event, event.getAuthor(), infoMessage);
		this.departures = departures;
	}
	
	@Override
	protected boolean onExecute(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args){
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
				Actions.deleteMessage(event.getMessage());
				if(filtered.stream().map(LuxBusDeparture::getDirection).distinct().count() < 2){
					filtered.stream().sorted().forEachOrdered(departure -> Actions.reply(event, departure.getAsEmbed(new EmbedBuilder()).build()));
				}
				else{
					LuxBusGetStopCommand.askDirection(event, filtered);
				}
				return true;
			}
		}
		return false;
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
