package fr.mrcraftcod.gunterdiscord.listeners;

import fr.mrcraftcod.gunterdiscord.settings.configs.QuestionsChannelConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.QuestionsFinalChannelConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.BasicEmotes;
import fr.mrcraftcod.gunterdiscord.utils.Log;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-06-05
 */
public class QuestionReactionListener extends ListenerAdapter
{
	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event)
	{
		super.onMessageReactionAdd(event);
		try
		{
			if(new QuestionsChannelConfig().isChannel(event.getTextChannel()))
			{
				if(!event.getUser().isBot())
				{
					BasicEmotes emote = BasicEmotes.getEmote(event.getReactionEmote().getName());
					if(emote == BasicEmotes.CHECK_OK)
					{
						event.getTextChannel().getMessageById(event.getReaction().getMessageIdLong()).queue(m -> {
							List<Message> messagesSent = Actions.getMessage(new QuestionsFinalChannelConfig().getTextChannel(event.getGuild()), m.getEmbeds().stream().map(Utilities::buildEmbed).map(mess -> mess.addField("Approved by", event.getUser().getAsMention(), false).setTimestamp(m.getCreationTime())).map(EmbedBuilder::build).collect(Collectors.toList()));
							messagesSent.forEach(mess -> mess.addReaction(BasicEmotes.CHECK_OK.getValue()).queue());
							Actions.deleteMessage(m);
							try
							{
								User user = m.getEmbeds().stream().flatMap(e -> e.getFields().stream()).filter(e -> e.getName().equals("Utilisateur")).map(e -> event.getJDA().getUserById(Long.parseLong(e.getValue().replaceAll("[^0-9]", "")))).findAny().orElse(null);
								String ID = m.getEmbeds().stream().flatMap(e -> e.getFields().stream()).filter(e -> e.getName().equals("ID")).map(MessageEmbed.Field::getValue).findAny().orElse("");
								if(user != null)
									Actions.replyPrivate(user, "Votre question (ID: " + ID + ") a été acceptée et transférée");
							}
							catch(Exception e)
							{
								e.printStackTrace();
							}
						});
					}
					else if(emote == BasicEmotes.CROSS_NO)
					{
						event.getTextChannel().getMessageById(event.getReaction().getMessageIdLong()).queue(m -> {
							Actions.deleteMessage(m);
							try
							{
								User user = m.getEmbeds().stream().flatMap(e -> e.getFields().stream()).filter(e -> e.getName().equals("Utilisateur")).map(e -> event.getJDA().getUserById(Long.parseLong(e.getValue().replaceAll("[^0-9]", "")))).findAny().orElse(null);
								String ID = m.getEmbeds().stream().flatMap(e -> e.getFields().stream()).filter(e -> e.getName().equals("ID")).map(MessageEmbed.Field::getValue).findAny().orElse("");
								if(user != null)
									Actions.replyPrivate(user, "Votre question (ID: " + ID + ") a été refusée");
							}
							catch(Exception e)
							{
								e.printStackTrace();
							}
						});
					}
				}
			}
			else if(new QuestionsFinalChannelConfig().isChannel(event.getTextChannel()))
			{
				if(!event.getUser().isBot())
				{
					BasicEmotes emote = BasicEmotes.getEmote(event.getReactionEmote().getName());
					if(emote == BasicEmotes.CHECK_OK)
						event.getChannel().getMessageById(event.getMessageId()).queue(Actions::deleteMessage);
				}
			}
		}
		catch(Exception e)
		{
			Log.error(e, "");
		}
	}
}
