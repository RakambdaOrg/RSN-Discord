package fr.mrcraftcod.gunterdiscord.listeners;

import fr.mrcraftcod.gunterdiscord.settings.NoValueDefinedException;
import fr.mrcraftcod.gunterdiscord.settings.configs.QuestionsChannelConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.QuestionsFinalChannelConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.BasicEmotes;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import fr.mrcraftcod.gunterdiscord.utils.log.Log;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import java.util.Objects;
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
					if(Objects.equals(emote, BasicEmotes.CHECK_OK)){
						final var message = event.getTextChannel().getHistory().getMessageById(event.getReaction().getMessageIdLong());
						{
							try{
								final var channel = new QuestionsFinalChannelConfig(event.getGuild()).getObject();
								Actions.sendMessage(channel, mess -> mess.addReaction(BasicEmotes.CHECK_OK.getValue()).queue(), message.getEmbeds().stream().map(Utilities::buildEmbed).map(mess -> mess.addField("Approved by", event.getUser().getAsMention(), false).setTimestamp(message.getTimeCreated())).map(EmbedBuilder::build).collect(Collectors.toList()));
								Actions.deleteMessage(message);
								try{
									final var user = message.getEmbeds().stream().flatMap(e -> e.getFields().stream()).filter(e -> e.getName().equals("User")).map(e -> event.getJDA().getUserById(Long.parseLong(NUMBER_ONLY.matcher(e.getValue()).replaceAll("")))).findAny().orElse(null);
									final var ID = message.getEmbeds().stream().flatMap(e -> e.getFields().stream()).filter(e -> e.getName().equals("ID")).map(MessageEmbed.Field::getValue).findAny().orElse("");
									if(Objects.nonNull(user)){
										Actions.replyPrivate(event.getGuild(), user, "Your question (ID: %s) has been accepted and forwarded.", ID);
									}
								}
								catch(final Exception e){
									Log.getLogger(event.getGuild()).error("Error handling question", e);
								}
							}
							catch(final NoValueDefinedException e){
								getLogger(event.getGuild()).error("Couldn't move message", e);
							}
						}
					}
					else if(Objects.equals(emote, BasicEmotes.CROSS_NO)){
						final var message = event.getTextChannel().getHistory().getMessageById(event.getReaction().getMessageIdLong());
						Actions.deleteMessage(message);
						try{
							final var user = message.getEmbeds().stream().flatMap(e -> e.getFields().stream()).filter(e -> e.getName().equals("User")).map(e -> event.getJDA().getUserById(Long.parseLong(NUMBER_ONLY.matcher(e.getValue()).replaceAll("")))).findAny().orElse(null);
							final var ID = message.getEmbeds().stream().flatMap(e -> e.getFields().stream()).filter(e -> e.getName().equals("ID")).map(MessageEmbed.Field::getValue).findAny().orElse("");
							if(Objects.nonNull(user)){
								Actions.replyPrivate(event.getGuild(), user, "Your question (ID: %s) has been rejected.", ID);
							}
						}
						catch(final Exception e){
							Log.getLogger(event.getGuild()).error("Error handling question", e);
						}
					}
				}
			}
			else if(new QuestionsFinalChannelConfig(event.getGuild()).isChannel(event.getTextChannel())){
				if(!event.getUser().isBot()){
					final var emote = BasicEmotes.getEmote(event.getReactionEmote().getName());
					if(Objects.equals(emote, BasicEmotes.CHECK_OK)){
						Actions.deleteMessage(event.getChannel().getHistory().getMessageById(event.getMessageId()));
					}
				}
			}
		}
		catch(final Exception e){
			getLogger(event.getGuild()).error("", e);
		}
	}
}
