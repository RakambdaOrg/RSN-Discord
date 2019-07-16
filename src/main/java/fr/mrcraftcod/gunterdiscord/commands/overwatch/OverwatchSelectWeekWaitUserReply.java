package fr.mrcraftcod.gunterdiscord.commands.overwatch;

import fr.mrcraftcod.gunterdiscord.listeners.reply.BasicWaitingUserReply;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.BasicEmotes;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import fr.mrcraftcod.gunterdiscord.utils.overwatch.stage.match.OverwatchCompetitor;
import fr.mrcraftcod.gunterdiscord.utils.overwatch.stage.match.OverwatchScore;
import fr.mrcraftcod.gunterdiscord.utils.overwatch.stage.week.OverwatchWeek;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import javax.annotation.Nonnull;
import java.awt.Color;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class OverwatchSelectWeekWaitUserReply extends BasicWaitingUserReply{
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm");
	private final Map<BasicEmotes, OverwatchWeek> options;
	
	public OverwatchSelectWeekWaitUserReply(Map<BasicEmotes, OverwatchWeek> options, GuildMessageReactionAddEvent event, Message infoMessage){
		super(event, event.getUser(), infoMessage);
		this.options = options;
	}
	
	@Override
	protected boolean onExecute(@Nonnull GuildMessageReceivedEvent event, @Nonnull LinkedList<String> args){
		return false;
	}
	
	@Override
	public boolean onExecute(@Nonnull GuildMessageReactionAddEvent event){
		if(!Objects.equals(event.getUser(), event.getJDA().getSelfUser())){
			final var replyEmote = BasicEmotes.getEmote(event.getReactionEmote().getName());
			if(Objects.nonNull(replyEmote)){
				if(this.options.containsKey(replyEmote)){
					final var week = options.get(replyEmote);
					final var builder = Utilities.buildEmbed(event.getUser(), Color.GREEN, week.getName());
					week.getMatches().forEach(m -> {
						final var message = m.hasEnded() ? m.getScores().stream().map(OverwatchScore::getValue).map(Object::toString).collect(Collectors.joining(" - ")) : ("On the " + m.getStartDate().format(FORMATTER));
						builder.addField(m.getCompetitors().stream().map(OverwatchCompetitor::getName).collect(Collectors.joining(" vs ")), message, false);
					});
					Actions.reply(event, builder.build());
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public long getEmoteMessageId(){
		return this.getInfoMessages().stream().map(Message::getIdLong).findAny().orElse(-1L);
	}
}
