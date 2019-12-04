package fr.raksrinana.rsndiscord.listeners;

import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.settings.types.TodoConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.log.Log;
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
			if(!event.getUser().isBot() && event.getReactionEmote().isEmoji()){
				final var emote = BasicEmotes.getEmote(event.getReactionEmote().getEmoji());
				if(Settings.get(event.getGuild()).getQuestionsConfiguration().getInputChannel().map(c -> Objects.equals(c.getChannelId(), event.getChannel().getIdLong())).orElse(false)){
					if(emote == BasicEmotes.CHECK_OK){
						handleQuestionOkEmote(event);
					}
					else if(emote == BasicEmotes.CROSS_NO){
						handleQuestionNoEmote(event);
					}
				}
				else if(Settings.get(event.getGuild()).getQuestionsConfiguration().getOutputChannel().map(c -> Objects.equals(c.getChannelId(), event.getChannel().getIdLong())).orElse(false)){
					if(emote == BasicEmotes.CHECK_OK){
						Utilities.getMessageById(event.getChannel(), event.getMessageIdLong()).thenAccept(Actions::deleteMessage);
					}
				}
				else{
					handleTodos(event, emote);
				}
			}
		}
		catch(final Exception e){
			Log.getLogger(event.getGuild()).error("", e);
		}
	}
	
	private void handleQuestionOkEmote(@NonNull GuildMessageReactionAddEvent event){
		Utilities.getMessageById(event.getChannel(), event.getMessageIdLong()).thenAccept(message -> Settings.get(event.getGuild()).getQuestionsConfiguration().getOutputChannel().flatMap(ChannelConfiguration::getChannel).ifPresentOrElse(channel -> {
			message.getEmbeds().stream().map(Utilities::copyEmbed).map(mess -> mess.addField("Approved by", event.getUser().getAsMention(), false).setTimestamp(message.getTimeCreated()).build()).forEach(embed -> Actions.sendMessage(channel, "", embed).thenAccept(mess -> Actions.addReaction(mess, BasicEmotes.CHECK_OK.getValue())));
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
	
	private void handleTodos(@NonNull GuildMessageReactionAddEvent event, @NonNull BasicEmotes emote){
		Settings.get(event.getGuild()).getTodos().stream().filter(todo -> Objects.equals(todo.getMessage().getChannel().getChannelId(), event.getChannel().getIdLong())).filter(todo -> Objects.equals(todo.getMessage().getMessageId(), event.getMessageIdLong())).findFirst().ifPresent(todo -> {
			if(emote == BasicEmotes.CHECK_OK){
				processTodoCompleted(event, todo);
			}
		});
	}
	
	private Optional<String> getIdFromQuestion(@NonNull Message message){
		return message.getEmbeds().stream().flatMap(e -> e.getFields().stream()).filter(e -> Objects.equals(e.getName(), "ID")).map(MessageEmbed.Field::getValue).findAny();
	}
	
	private Optional<User> getUserFomQuestion(@NonNull Message message){
		return message.getEmbeds().stream().flatMap(e -> e.getFields().stream()).filter(e -> Objects.equals(e.getName(), "User")).map(MessageEmbed.Field::getValue).filter(Objects::nonNull).map(e -> Main.getJda().getUserById(Long.parseLong(NUMBER_ONLY.matcher(e).replaceAll("")))).findAny();
	}
	
	private void processTodoCompleted(@NonNull GuildMessageReactionAddEvent event, @NonNull TodoConfiguration todo){
		todo.getMessage().getMessage().ifPresent(message -> {
			if(Objects.equals(todo.getMessage().getChannel().getChannelId(), Settings.get(event.getGuild()).getAniListConfiguration().getThaChannel().map(ChannelConfiguration::getChannelId).orElse(null))){
				Optional.ofNullable(event.getJDA().getUserById(Utilities.RAKSRINANA_ACCOUNT)).map(User::openPrivateChannel).ifPresent(user -> user.queue(privateChannel -> message.getEmbeds().forEach(embed -> Actions.sendPrivateMessage(event.getGuild(), privateChannel, event.getMember().getUser().getAsMention() + " completed", embed))));
			}
			if(todo.isDeleteOnDone()){
				Actions.deleteMessage(message);
			}
			else{
				Actions.editMessage(message, BasicEmotes.OK_HAND.getValue() + " __**DONE**__:  " + message.getContentRaw());
				Actions.clearReactions(message);
				message.clearReactions().queue();
				if(message.isPinned()){
					Actions.unpin(message);
				}
			}
			Settings.get(event.getGuild()).removeTodo(todo);
		});
	}
}
