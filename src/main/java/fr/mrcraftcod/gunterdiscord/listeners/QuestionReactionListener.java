package fr.mrcraftcod.gunterdiscord.listeners;

import fr.mrcraftcod.gunterdiscord.settings.NoValueDefinedException;
import fr.mrcraftcod.gunterdiscord.settings.configs.QuestionsChannelConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.QuestionsFinalChannelConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.BasicEmotes;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import fr.mrcraftcod.gunterdiscord.utils.log.Log;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-06-05
 */
public class QuestionReactionListener extends ListenerAdapter{
	private static final Pattern NUMBER_ONLY = Pattern.compile("[^0-9]");
	
	@Override
	public void onMessageReactionAdd(final MessageReactionAddEvent event){
		super.onMessageReactionAdd(event);
		try{
			if(new QuestionsChannelConfig(event.getGuild()).isChannel(event.getTextChannel())){
				if(!event.getUser().isBot()){
					final var emote = BasicEmotes.getEmote(event.getReactionEmote().getName());
					if(emote == BasicEmotes.CHECK_OK){
						event.getTextChannel().getMessageById(event.getReaction().getMessageIdLong()).queue(m -> {
							try{
								final var channel = new QuestionsFinalChannelConfig(event.getGuild()).getObject();
								final var messagesSent = Actions.getMessage(channel, m.getEmbeds().stream().map(Utilities::buildEmbed).map(mess -> mess.addField("Approved by", event.getUser().getAsMention(), false).setTimestamp(m.getCreationTime())).map(EmbedBuilder::build).collect(Collectors.toList()));
								messagesSent.forEach(mess -> mess.addReaction(BasicEmotes.CHECK_OK.getValue()).queue());
								Actions.deleteMessage(m);
								try{
									final var user = m.getEmbeds().stream().flatMap(e -> e.getFields().stream()).filter(e -> e.getName().equals("Utilisateur")).map(e -> event.getJDA().getUserById(Long.parseLong(NUMBER_ONLY.matcher(e.getValue()).replaceAll("")))).findAny().orElse(null);
									final var ID = m.getEmbeds().stream().flatMap(e -> e.getFields().stream()).filter(e -> e.getName().equals("ID")).map(MessageEmbed.Field::getValue).findAny().orElse("");
									if(user != null){
										Actions.replyPrivate(event.getGuild(), user, "Votre question (ID: %s) a été acceptée et transférée", ID);
									}
								}
								catch(final Exception e){
									Log.getLogger(event.getGuild()).error("Error handling question", e);
								}
							}
							catch(final NoValueDefinedException e){
								getLogger(event.getGuild()).error("Couldn't move message", e);
							}
						});
					}
					else if(emote == BasicEmotes.CROSS_NO){
						event.getTextChannel().getMessageById(event.getReaction().getMessageIdLong()).queue(m -> {
							Actions.deleteMessage(m);
							try{
								final var user = m.getEmbeds().stream().flatMap(e -> e.getFields().stream()).filter(e -> e.getName().equals("Utilisateur")).map(e -> event.getJDA().getUserById(Long.parseLong(NUMBER_ONLY.matcher(e.getValue()).replaceAll("")))).findAny().orElse(null);
								final var ID = m.getEmbeds().stream().flatMap(e -> e.getFields().stream()).filter(e -> e.getName().equals("ID")).map(MessageEmbed.Field::getValue).findAny().orElse("");
								if(user != null){
									Actions.replyPrivate(event.getGuild(), user, "Votre question (ID: %s) a été refusée", ID);
								}
							}
							catch(final Exception e){
								Log.getLogger(event.getGuild()).error("Error handling question", e);
							}
						});
					}
				}
			}
			else if(new QuestionsFinalChannelConfig(event.getGuild()).isChannel(event.getTextChannel())){
				if(!event.getUser().isBot()){
					final var emote = BasicEmotes.getEmote(event.getReactionEmote().getName());
					if(emote == BasicEmotes.CHECK_OK){
						event.getChannel().getMessageById(event.getMessageId()).queue(Actions::deleteMessage);
					}
				}
			}
		}
		catch(final Exception e){
			getLogger(event.getGuild()).error("", e);
		}
	}
}
