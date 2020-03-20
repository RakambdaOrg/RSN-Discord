package fr.raksrinana.rsndiscord.listeners;

import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.WaitingReactionMessageConfiguration;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.settings.types.MessageConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.log.Log;
import fr.raksrinana.rsndiscord.utils.reaction.ReactionHandlerResult;
import fr.raksrinana.rsndiscord.utils.reaction.ReactionTag;
import fr.raksrinana.rsndiscord.utils.reaction.ReactionUtils;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

public class ReactionListener extends ListenerAdapter{
	private static final Pattern NUMBER_ONLY = Pattern.compile("[^0-9]");
	
	@Override
	public void onGuildMessageReactionAdd(@NonNull final GuildMessageReactionAddEvent event){
		super.onGuildMessageReactionAdd(event);
		try{
			if(!event.getUser().isBot()){
				if(event.getReactionEmote().isEmoji()){
					final var emote = BasicEmotes.getEmote(event.getReactionEmote().getEmoji());
					if(Settings.get(event.getGuild()).getQuestionsConfiguration().getInputChannel().map(c -> Objects.equals(c.getChannelId(), event.getChannel().getIdLong())).orElse(false)){
						if(emote == BasicEmotes.CHECK_OK){
							handleQuestionOkEmote(event);
						}
						else if(emote == BasicEmotes.CROSS_NO){
							handleQuestionNoEmote(event);
						}
					}
				}
				final var it = Settings.get(event.getGuild()).getMessagesAwaitingReaction();
				while(it.hasNext()){
					final var waitingReactionMessage = it.next();
					if(Objects.equals(waitingReactionMessage.getMessage().getMessageId(), event.getMessageIdLong()) && Objects.equals(waitingReactionMessage.getMessage().getChannel().getChannelId(), event.getChannel().getIdLong())){
						for(final var handler : ReactionUtils.getHandlers()){
							if(handler.acceptTag(waitingReactionMessage.getTag())){
								final var result = handler.accept(event, waitingReactionMessage);
								if(result == ReactionHandlerResult.PROCESSED_DELETE){
									it.remove();
								}
								if(result.isTerminal()){
									break;
								}
							}
						}
					}
				}
			}
		}
		catch(final Exception e){
			Log.getLogger(event.getGuild()).error("", e);
		}
	}
	
	private void handleQuestionOkEmote(@NonNull GuildMessageReactionAddEvent event){
		Utilities.getMessageById(event.getChannel(), event.getMessageIdLong()).thenAccept(message -> Settings.get(event.getGuild()).getQuestionsConfiguration().getOutputChannel().flatMap(ChannelConfiguration::getChannel).ifPresentOrElse(channel -> {
			message.getEmbeds().stream().map(Utilities::copyEmbed).map(mess -> mess.addField("Approved by", event.getUser().getAsMention(), false).setTimestamp(message.getTimeCreated()).build()).forEach(embed -> Actions.sendMessage(channel, "", embed).thenAccept(mess -> {
				Settings.get(event.getGuild()).addMessagesAwaitingReaction(new WaitingReactionMessageConfiguration(new MessageConfiguration(event.getChannel().getIdLong(), event.getMessageIdLong()), ReactionTag.ACCEPTED_QUESTION, null));
				Actions.addReaction(mess, BasicEmotes.CHECK_OK.getValue());
			}));
			Actions.deleteMessage(message);
			final var text = MessageFormat.format("Your question (ID: {0}) has been accepted and forwarded.", getIdFromQuestion(message).orElse(""));
			getUserFomQuestion(message).ifPresent(user -> Actions.replyPrivate(event.getGuild(), user, text, null));
		}, () -> Log.getLogger(event.getGuild()).error("Couldn't move message")));
	}
	
	private void handleQuestionNoEmote(@NonNull GuildMessageReactionAddEvent event){
		Utilities.getMessageById(event.getChannel(), event.getMessageIdLong()).thenAccept(message -> {
			Actions.deleteMessage(message);
			final var text = MessageFormat.format("Your question (ID: {0}) has been rejected.", getIdFromQuestion(message).orElse(""));
			getUserFomQuestion(message).ifPresent(value -> Actions.replyPrivate(event.getGuild(), value, text, null));
		});
	}
	
	private Optional<String> getIdFromQuestion(@NonNull Message message){
		return message.getEmbeds().stream().flatMap(e -> e.getFields().stream()).filter(e -> Objects.equals(e.getName(), "ID")).map(MessageEmbed.Field::getValue).findAny();
	}
	
	private Optional<User> getUserFomQuestion(@NonNull Message message){
		return message.getEmbeds().stream().flatMap(e -> e.getFields().stream()).filter(e -> Objects.equals(e.getName(), "User")).map(MessageEmbed.Field::getValue).filter(Objects::nonNull).map(e -> Main.getJda().retrieveUserById(Long.parseLong(NUMBER_ONLY.matcher(e).replaceAll(""))).complete()).findAny();
	}
}
