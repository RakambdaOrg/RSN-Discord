package fr.raksrinana.rsndiscord.listeners;

import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.log.Log;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-06-05
 */
public class ReactionListener extends ListenerAdapter{
	private static final Pattern NUMBER_ONLY = Pattern.compile("[^0-9]");
	
	@SuppressWarnings("DuplicatedCode")
	@Override
	public void onGuildMessageReactionAdd(@Nonnull final GuildMessageReactionAddEvent event){
		super.onGuildMessageReactionAdd(event);
		try{
			if(!event.getUser().isBot()){
				final var emote = BasicEmotes.getEmote(event.getReactionEmote().getName());
				if(Settings.getConfiguration(event.getGuild()).getQuestionsConfiguration().getInputChannel().map(c -> Objects.equals(c.getChannelId(), event.getChannel().getIdLong())).orElse(false)){
					if(emote == BasicEmotes.CHECK_OK){
						event.getChannel().getHistoryAround(event.getMessageIdLong(), 1).queue(hist -> {
							final var message = hist.getMessageById(event.getReaction().getMessageIdLong());
							if(Objects.nonNull(message)){
								Settings.getConfiguration(event.getGuild()).getQuestionsConfiguration().getOutputChannel().flatMap(ChannelConfiguration::getChannel).ifPresentOrElse(channel -> {
									Actions.sendMessage(channel, mess -> mess.addReaction(BasicEmotes.CHECK_OK.getValue()).queue(), message.getEmbeds().stream().map(Utilities::buildEmbed).map(mess -> mess.addField("Approved by", event.getUser().getAsMention(), false).setTimestamp(message.getTimeCreated())).map(EmbedBuilder::build).collect(Collectors.toList()));
									Actions.deleteMessage(message);
									try{
										final var user = message.getEmbeds().stream().flatMap(e -> e.getFields().stream()).filter(e -> Objects.equals(e.getName(), "User")).map(MessageEmbed.Field::getValue).filter(Objects::nonNull).map(e -> event.getJDA().getUserById(Long.parseLong(NUMBER_ONLY.matcher(e).replaceAll("")))).findAny();
										final var ID = message.getEmbeds().stream().flatMap(e -> e.getFields().stream()).filter(e -> Objects.equals(e.getName(), "ID")).map(MessageEmbed.Field::getValue).findAny();
										user.ifPresent(value -> Actions.replyPrivate(event.getGuild(), value, "Your question (ID: %s) has been accepted and forwarded.", ID.orElse("")));
									}
									catch(final Exception e){
										Log.getLogger(event.getGuild()).error("Error handling question", e);
									}
								}, () -> Log.getLogger(event.getGuild()).error("Couldn't move message"));
							}
						});
					}
					else if(emote == BasicEmotes.CROSS_NO){
						event.getChannel().getHistoryAround(event.getMessageIdLong(), 1).queue(hist -> {
							final var message = hist.getMessageById(event.getReaction().getMessageIdLong());
							if(Objects.nonNull(message)){
								Actions.deleteMessage(message);
								try{
									final var user = message.getEmbeds().stream().flatMap(e -> e.getFields().stream()).filter(e -> Objects.equals(e.getName(), "User")).map(MessageEmbed.Field::getValue).filter(Objects::nonNull).map(e -> event.getJDA().getUserById(Long.parseLong(NUMBER_ONLY.matcher(e).replaceAll("")))).findAny();
									final var ID = message.getEmbeds().stream().flatMap(e -> e.getFields().stream()).filter(e -> Objects.equals(e.getName(), "ID")).map(MessageEmbed.Field::getValue).findAny();
									user.ifPresent(value -> Actions.replyPrivate(event.getGuild(), value, "Your question (ID: %s) has been rejected.", ID.orElse("")));
								}
								catch(final Exception e){
									Log.getLogger(event.getGuild()).error("Error handling question", e);
								}
							}
						});
					}
				}
				else if(Settings.getConfiguration(event.getGuild()).getQuestionsConfiguration().getOutputChannel().map(c -> Objects.equals(c.getChannelId(), event.getChannel().getIdLong())).orElse(false)){
					if(emote == BasicEmotes.CHECK_OK){
						event.getChannel().getHistoryAround(event.getMessageIdLong(), 1).queue(hist -> Actions.deleteMessage(hist.getMessageById(event.getMessageIdLong())));
					}
				}
				else{
					Settings.getConfiguration(event.getGuild()).getTodos().stream().filter(todo -> Objects.equals(todo.getMessage().getChannel().getChannelId(), event.getChannel().getIdLong())).filter(todo -> Objects.equals(todo.getMessage().getMessageId(), event.getMessageIdLong())).findFirst().ifPresent(todo -> {
						if(emote == BasicEmotes.CHECK_OK){
							todo.getMessage().getMessage().ifPresent(message -> {
								if(Settings.getConfiguration(event.getGuild()).getTodos().stream().map(t -> t.getMessage().getChannel().getChannelId()).anyMatch(cId -> Objects.equals(cId, event.getChannel().getIdLong()))){
									Optional.ofNullable(event.getJDA().getUserById(Utilities.RAKSRINANA_ACCOUNT)).map(User::openPrivateChannel).ifPresent(user -> user.queue(privateChannel -> message.getEmbeds().forEach(embed -> Actions.sendPrivateMessage(event.getGuild(), privateChannel, event.getMember().getUser().getAsMention() + " completed", embed))));
								}
								if(todo.isDeleteOnDone()){
									message.delete().queue();
								}
								else{
									message.editMessage(BasicEmotes.OK_HAND.getValue() + " __**DONE**__:  " + message.getContentRaw()).queue();
									message.clearReactions().queue();
									if(message.isPinned()){
										message.unpin().queue();
									}
								}
								Settings.getConfiguration(event.getGuild()).removeTodo(todo);
							});
						}
					});
				}
			}
		}
		catch(final Exception e){
			Log.getLogger(event.getGuild()).error("", e);
		}
	}
}
