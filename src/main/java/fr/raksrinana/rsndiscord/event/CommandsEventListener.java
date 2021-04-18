package fr.raksrinana.rsndiscord.event;

import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.api.uselessfacts.UselessFactsApi;
import fr.raksrinana.rsndiscord.command.BotCommand;
import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.NotAllowedException;
import fr.raksrinana.rsndiscord.command.NotHandledException;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.settings.GuildConfiguration;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.reaction.WaitingReactionMessageConfiguration;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import static fr.raksrinana.rsndiscord.command.CommandResult.BAD_ARGUMENTS;
import static fr.raksrinana.rsndiscord.command.CommandResult.FAILED;
import static fr.raksrinana.rsndiscord.command.DeleteMode.AFTER;
import static fr.raksrinana.rsndiscord.command.DeleteMode.BEFORE;
import static fr.raksrinana.rsndiscord.reaction.ReactionTag.EXTERNAL_TODO;
import static fr.raksrinana.rsndiscord.reaction.ReactionTag.TODO;
import static fr.raksrinana.rsndiscord.reaction.ReactionUtils.DELETE_KEY;
import static fr.raksrinana.rsndiscord.schedule.ScheduleUtils.deleteMessage;
import static fr.raksrinana.rsndiscord.utils.BasicEmotes.*;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static fr.raksrinana.rsndiscord.utils.Utilities.containsChannel;
import static fr.raksrinana.rsndiscord.utils.Utilities.getAllAnnotatedWith;
import static java.awt.Color.*;
import static net.dv8tion.jda.api.entities.MessageType.CHANNEL_PINNED_ADD;

@EventListener
@Getter
public class CommandsEventListener extends ListenerAdapter{
	public final static String DEFAULT_PREFIX = System.getProperty("RSN_DEFAULT_PREFIX", Main.DEVELOPMENT ? "g2?" : "g?");
	private final Set<Command> commands;
	
	/**
	 * Constructor.
	 */
	public CommandsEventListener(){
		commands = getAllAnnotatedWith(BotCommand.class, clazz -> (Command) clazz.getConstructor().newInstance())
				.peek(c -> Log.getLogger().info("Loaded command {}", c.getClass().getName()))
				.collect(Collectors.toSet());
		
		var counts = new HashMap<String, Integer>();
		commands.forEach(command -> command.getCommandStrings()
				.forEach(cmd -> counts.put(cmd, counts.getOrDefault(cmd, 0) + 1)));
		
		var clash = counts.keySet().stream()
				.filter(key -> counts.get(key) > 1)
				.collect(Collectors.joining(", "));
		if(!clash.isEmpty()){
			Log.getLogger().error("Command clash: {}", clash);
		}
	}
	
	@Override
	public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event){
		super.onGuildMessageReceived(event);
		try{
			var guildConfiguration = Settings.get(event.getGuild());
			var message = event.getMessage();
			var author = event.getAuthor();
			var channel = event.getChannel();
			
			if(message.getType() != MessageType.DEFAULT){
				return;
			}
			
			if(isCommand(event.getGuild(), message.getContentRaw())){
				if(author.isBot()){
					return;
				}
				processCommand(event);
			}
			else{
				if(containsChannel(guildConfiguration.getReactionsConfiguration().getAutoTodoChannels(), channel)){
					if(message.getType() == CHANNEL_PINNED_ADD){
						JDAWrappers.delete(message).submit();
					}
					else{
						var waitingReactionMessageConfiguration = new WaitingReactionMessageConfiguration(message,
								TODO, Map.of(DELETE_KEY, Boolean.toString(true)));
						guildConfiguration.addMessagesAwaitingReaction(waitingReactionMessageConfiguration);
						
						JDAWrappers.addReaction(message, CHECK_OK).submit();
						JDAWrappers.addReaction(message, PAPERCLIP).submit();
						JDAWrappers.addReaction(message, RIGHT_ARROW_CURVING_LEFT).submit();
					}
				}
				else if(isExternalTodoChannel(guildConfiguration, channel)){
					var waitingReactionMessageConfiguration = new WaitingReactionMessageConfiguration(message,
							EXTERNAL_TODO, Map.of(DELETE_KEY, Boolean.toString(false)));
					guildConfiguration.addMessagesAwaitingReaction(waitingReactionMessageConfiguration);
					
					JDAWrappers.addReaction(message, CHECK_OK).submit();
				}
			}
		}
		catch(Exception e){
			Log.getLogger(event.getGuild()).error("Error handling message", e);
		}
	}
	
	@Override
	public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent event){
		super.onPrivateMessageReceived(event);
		var author = event.getAuthor();
		try{
			var self = event.getJDA().getSelfUser();
			if(author.isBot()){
				return;
			}
			
			Log.getLogger().info("Received private message from {}: {}", author, event.getMessage());
			UselessFactsApi.getFact().ifPresentOrElse(fact -> {
				Log.getLogger().debug("Sending random fact: {}", fact);
				var builder = new EmbedBuilder().setAuthor(self.getName(), null, self.getAvatarUrl())
						.setColor(GREEN)
						.setTitle("Random fact");
				fact.fillEmbed(builder);
				
				JDAWrappers.message(event, "I don't really know what to answer, but here's a random fact for you")
						.embed(builder.build())
						.submit();
			}, () -> JDAWrappers.message(event, "I just farted").submit());
		}
		catch(Exception e){
			Log.getLogger().error("Error private message from {}", author, e);
		}
	}
	
	/**
	 * Tell if this text is a command.
	 *
	 * @param guild The guild.
	 * @param text  The text.
	 *
	 * @return True if a command, false otherwise.
	 */
	private static boolean isCommand(@NotNull Guild guild, @NotNull String text){
		return text.startsWith(Settings.get(guild).getPrefix().orElse(DEFAULT_PREFIX));
	}
	
	private void processCommand(@NotNull GuildMessageReceivedEvent event){
		var guild = event.getGuild();
		var author = event.getAuthor();
		var message = event.getMessage();
		var messageDeleted = new AtomicBoolean(false);
		
		Log.getLogger(guild).debug("Processing potential command from {}: {}", author, message.getContentRaw());
		var args = new LinkedList<>(Arrays.asList(message.getContentRaw().split(" ")));
		var cmdText = args.pop().substring(Settings.get(guild).getPrefix().orElse(DEFAULT_PREFIX).length());
		
		getCommand(cmdText).ifPresentOrElse(command -> {
			if(command.getDeleteMode() == BEFORE){
				JDAWrappers.delete(message).submit();
				messageDeleted.set(true);
			}
			try{
				Log.getLogger(guild).info("Executing command `{}`({}) from {}, args: {}", cmdText, command.getName(guild), author, args);
				var executionResult = command.execute(event, args);
				if(executionResult == FAILED){
					author.openPrivateChannel().submit()
							.thenAccept(privateChannel -> JDAWrappers.message(privateChannel, translate(guild, "listeners.commands.error")).submit());
				}
				else if(executionResult == BAD_ARGUMENTS){
					author.openPrivateChannel().submit()
							.thenAccept(privateChannel -> JDAWrappers.message(privateChannel, translate(guild, "listeners.commands.invalid-arguments")).submit());
				}
			}
			catch(NotAllowedException e){
				Log.getLogger(guild).error("Error executing command {} (not allowed)", command, e);
				var embed = new EmbedBuilder()
						.setAuthor(author.getName(), null, author.getAvatarUrl())
						.setColor(RED)
						.setTitle(translate(guild, "listeners.commands.unauthorized"))
						.build();
				JDAWrappers.message(event, embed).submit();
			}
			catch(NotHandledException e){
				Log.getLogger(guild).warn("Command {} isn't handled for {} ({})", command, author, e.getMessage());
			}
			catch(Exception e){
				Log.getLogger(guild).error("Error executing command {}", command, e);
				var embed = new EmbedBuilder()
						.setAuthor(author.getName(), null, author.getAvatarUrl())
						.setColor(RED)
						.setTitle(translate(guild, "listeners.commands.exception.title"))
						.addField(translate(guild, "listeners.commands.exception.kind"), e.getClass().getName(), false)
						.build();
				JDAWrappers.message(event, embed).submit();
			}
			
			if(command.getDeleteMode() == AFTER){
				JDAWrappers.delete(message).submit();
			}
		}, () -> {
			var embed = new EmbedBuilder()
					.setAuthor(author.getName(), null, author.getAvatarUrl())
					.setColor(ORANGE)
					.setTitle(translate(guild, "listeners.commands.not-found.title"))
					.addField(translate(guild, "listeners.commands.exception.command"), cmdText, false)
					.build();
			JDAWrappers.message(event, embed).submit()
					.thenAccept(deleteMessage(date -> date.plusMinutes(5)));
			JDAWrappers.delete(message).submit();
		});
	}
	
	private boolean isExternalTodoChannel(@NotNull GuildConfiguration guildConfiguration, @NotNull TextChannel channel){
		return guildConfiguration.getExternalTodos()
				.getNotificationChannel()
				.map(ChannelConfiguration::getChannelId)
				.map(id -> Objects.equals(id, channel.getIdLong()))
				.orElse(false);
	}
	
	/**
	 * get the command associated to this string.
	 *
	 * @param commandText The command text.
	 *
	 * @return The command or null if not found.
	 */
	@NotNull
	private Optional<Command> getCommand(@NotNull String commandText){
		return commands.stream().filter(command -> command.getCommandStrings().contains(commandText)).findFirst();
	}
}
