package fr.mrcraftcod.gunterdiscord.listeners;

import fr.mrcraftcod.gunterdiscord.settings.configs.QuestionsChannelConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.QuestionsFinalChannelConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.BasicEmotes;
import fr.mrcraftcod.gunterdiscord.utils.Log;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

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
			if(event.getChannel().getIdLong() == new QuestionsChannelConfig().getTextChannel(event.getGuild()).getIdLong())
			{
				if(!event.getUser().isBot())
				{
					BasicEmotes emote = BasicEmotes.getEmote(event.getReactionEmote().getName());
					if(emote == BasicEmotes.CHECK_OK)
					{
						event.getTextChannel().getMessageById(event.getReaction().getMessageIdLong()).queue(m -> {
							Actions.sendMessage(new QuestionsFinalChannelConfig().getTextChannel(event.getGuild()), m.getEmbeds());
							Actions.deleteMessage(m);
						});
					}
					else if(emote == BasicEmotes.CROSS_NO)
						event.getTextChannel().getMessageById(event.getReaction().getMessageIdLong()).queue(Actions::deleteMessage);
				}
			}
		}
		catch(Exception e)
		{
			Log.error("", e);
		}
	}
}
