package fr.mrcraftcod.gunterdiscord.listeners;

import fr.mrcraftcod.gunterdiscord.settings.NoValueDefinedException;
import fr.mrcraftcod.gunterdiscord.settings.configs.QuestionsChannelConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.QuestionsFinalChannelConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.BasicEmotes;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import java.util.List;
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
	public void onMessageReactionAdd(MessageReactionAddEvent event){
		super.onMessageReactionAdd(event);
		try{
			if(new QuestionsChannelConfig(event.getGuild()).isChannel(event.getTextChannel())){
				if(!event.getUser().isBot()){
					BasicEmotes emote = BasicEmotes.getEmote(event.getReactionEmote().getName());
					if(emote == BasicEmotes.CHECK_OK){
						event.getTextChannel().getMessageById(event.getReaction().getMessageIdLong()).queue(m -> {
							try{
								TextChannel channel = new QuestionsFinalChannelConfig(event.getGuild()).getObject();
								List<Message> messagesSent = Actions.getMessage(channel, m.getEmbeds().stream().map(Utilities::buildEmbed).map(mess -> mess.addField("Approved by", event.getUser().getAsMention(), false).setTimestamp(m.getCreationTime())).map(EmbedBuilder::build).collect(Collectors.toList()));
								messagesSent.forEach(mess -> mess.addReaction(BasicEmotes.CHECK_OK.getValue()).queue());
								Actions.deleteMessage(m);
								try{
									User user = m.getEmbeds().stream().flatMap(e -> e.getFields().stream()).filter(e -> e.getName().equals("Utilisateur")).map(e -> event.getJDA().getUserById(Long.parseLong(NUMBER_ONLY.matcher(e.getValue()).replaceAll("")))).findAny().orElse(null);
									String ID = m.getEmbeds().stream().flatMap(e -> e.getFields().stream()).filter(e -> e.getName().equals("ID")).map(MessageEmbed.Field::getValue).findAny().orElse("");
									if(user != null){
										Actions.replyPrivate(event.getGuild(), user, "Votre question (ID: %s) a été acceptée et transférée", ID);
									}
								}
								catch(Exception e){
									e.printStackTrace();
								}
							}
							catch(NoValueDefinedException e){
								getLogger(event.getGuild()).error("Couldn't move message", e);
							}
						});
					}
					else if(emote == BasicEmotes.CROSS_NO){
						event.getTextChannel().getMessageById(event.getReaction().getMessageIdLong()).queue(m -> {
							Actions.deleteMessage(m);
							try{
								User user = m.getEmbeds().stream().flatMap(e -> e.getFields().stream()).filter(e -> e.getName().equals("Utilisateur")).map(e -> event.getJDA().getUserById(Long.parseLong(NUMBER_ONLY.matcher(e.getValue()).replaceAll("")))).findAny().orElse(null);
								String ID = m.getEmbeds().stream().flatMap(e -> e.getFields().stream()).filter(e -> e.getName().equals("ID")).map(MessageEmbed.Field::getValue).findAny().orElse("");
								if(user != null){
									Actions.replyPrivate(event.getGuild(), user, "Votre question (ID: %s) a été refusée", ID);
								}
							}
							catch(Exception e){
								e.printStackTrace();
							}
						});
					}
				}
			}
			else if(new QuestionsFinalChannelConfig(event.getGuild()).isChannel(event.getTextChannel())){
				if(!event.getUser().isBot()){
					BasicEmotes emote = BasicEmotes.getEmote(event.getReactionEmote().getName());
					if(emote == BasicEmotes.CHECK_OK){
						event.getChannel().getMessageById(event.getMessageId()).queue(Actions::deleteMessage);
					}
				}
			}
		}
		catch(Exception e){
			getLogger(event.getGuild()).error("", e);
		}
	}
}
