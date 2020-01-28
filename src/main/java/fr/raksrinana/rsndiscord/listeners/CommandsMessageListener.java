package fr.raksrinana.rsndiscord.listeners;

import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.commands.generic.*;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.WaitingReactionMessageConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import fr.raksrinana.rsndiscord.utils.log.Log;
import fr.raksrinana.rsndiscord.utils.reaction.ReactionTag;
import fr.raksrinana.rsndiscord.utils.reaction.ReactionUtils;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.reflections.Reflections;
import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class CommandsMessageListener extends ListenerAdapter{
	public final static String defaultPrefix = System.getProperty("RSN_DEFAULT_PREFIX", Main.DEVELOPMENT ? "g2?" : "g?");
	private final Set<Command> commands;
	
	/**
	 * Constructor.
	 */
	public CommandsMessageListener(){
		final var counts = new HashMap<String, Integer>();
		this.commands = new Reflections(Main.class.getPackage().getName() + ".commands").getTypesAnnotatedWith(BotCommand.class).stream().map(c -> {
			try{
				return c.getConstructor().newInstance();
			}
			catch(InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e){
				Log.getLogger(null).error("Failed to create instance of {}", c.getName(), e);
			}
			return null;
		}).filter(Objects::nonNull).filter(c -> c instanceof Command).map(c -> (Command) c).peek(c -> Log.getLogger(null).info("Loaded command {}", c.getClass().getName())).collect(Collectors.toSet());
		this.commands.forEach(command -> command.getCommandStrings().forEach(cmd -> counts.put(cmd, counts.getOrDefault(cmd, 0) + 1)));
		final var clash = counts.keySet().stream().filter(key -> counts.get(key) > 1).collect(Collectors.joining(", "));
		if(!clash.isEmpty()){
			Log.getLogger(null).error("Command clash: {}", clash);
		}
	}
	
	@Override
	public void onGuildMessageReceived(@NonNull final GuildMessageReceivedEvent event){
		super.onGuildMessageReceived(event);
		try{
			if(isCommand(event.getGuild(), event.getMessage().getContentRaw())){
				if(!event.getAuthor().isBot()){
					Log.getLogger(event.getGuild()).debug("Processing potential command from {}: {}", event.getAuthor(), event.getMessage().getContentRaw());
					Actions.deleteMessage(event.getMessage());
					final var args = new LinkedList<>(Arrays.asList(event.getMessage().getContentRaw().split(" ")));
					final var cmdText = args.pop().substring(Settings.get(event.getGuild()).getPrefix().orElse(defaultPrefix).length());
					this.getCommand(cmdText).ifPresentOrElse(command -> {
						try{
							Log.getLogger(event.getGuild()).info("Executing command `{}`({}) from {}, args: {}", cmdText, command.getName(), event.getAuthor(), args);
							final var executionResult = command.execute(event, args);
							if(executionResult == CommandResult.FAILED){
								Actions.replyPrivate(event.getGuild(), event.getAuthor(), "An error occurred", null);
							}
							else if(executionResult == CommandResult.BAD_ARGUMENTS){
								Actions.replyPrivate(event.getGuild(), event.getAuthor(), "The given arguments aren't valid. Please check the help.", null);
							}
						}
						catch(final NotAllowedException e){
							Log.getLogger(event.getGuild()).error("Error executing command {} (not allowed)", command, e);
							final var builder = new EmbedBuilder();
							builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
							builder.setColor(Color.RED);
							builder.setTitle("You're not allowed to execute this command");
							Actions.reply(event, "", builder.build());
						}
						catch(final NotHandledException e){
							Log.getLogger(event.getGuild()).warn("Command {} isn't handled for {} ({})", command, event.getAuthor(), e.getMessage());
						}
						catch(final Exception e){
							Log.getLogger(event.getGuild()).error("Error executing command {}", command, e);
							final var builder = new EmbedBuilder();
							builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
							builder.setColor(Color.RED);
							builder.setTitle("Something exploded");
							builder.addField("Exception kind", e.getClass().getName(), false);
							Actions.reply(event, "", builder.build());
						}
					}, () -> {
						final var builder = new EmbedBuilder();
						builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
						builder.setColor(Color.ORANGE);
						builder.setTitle("Command not found");
						builder.addField("Command", cmdText, false);
						Actions.reply(event, "", builder.build());
					});
				}
			}
			else if(Settings.get(event.getGuild()).getAutoTodoChannels().stream().anyMatch(channelConfiguration -> Objects.equals(channelConfiguration.getChannelId(), event.getChannel().getIdLong()))){
				if(event.getMessage().getType() == MessageType.CHANNEL_PINNED_ADD){
					Actions.deleteMessage(event.getMessage());
				}
				else{
					Actions.addReaction(event.getMessage(), BasicEmotes.CHECK_OK.getValue());
					Settings.get(event.getGuild()).addMessagesAwaitingReaction(new WaitingReactionMessageConfiguration(event.getMessage(), ReactionTag.TODO, Map.of(ReactionUtils.DELETE_KEY, Boolean.toString(true))));
					Actions.pin(event.getMessage());
				}
			}
		}
		catch(final Exception e){
			Log.getLogger(event.getGuild()).error("Error handling message", e);
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
	private static boolean isCommand(@NonNull final Guild guild, @NonNull final String text){
		return text.startsWith(Settings.get(guild).getPrefix().orElse(defaultPrefix));
	}
	
	/**
	 * get the command associated to this string.
	 *
	 * @param commandText The command text.
	 *
	 * @return The command or null if not found.
	 */
	@NonNull
	private Optional<Command> getCommand(@NonNull final String commandText){
		return this.commands.stream().filter(command -> command.getCommandStrings().contains(commandText.toLowerCase())).findFirst();
	}
}
