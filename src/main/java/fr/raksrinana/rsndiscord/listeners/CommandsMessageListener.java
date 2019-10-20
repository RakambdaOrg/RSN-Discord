package fr.raksrinana.rsndiscord.listeners;

import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.commands.generic.*;
import fr.raksrinana.rsndiscord.settings.NewSettings;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.log.Log;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.reflections.Reflections;
import javax.annotation.Nonnull;
import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 09/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-09
 */
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
				Log.getLogger(null).error("Failed to create instance of {}", c.getName());
			}
			return null;
		}).filter(Objects::nonNull).filter(c -> c instanceof Command).map(c -> (Command) c).peek(c -> Log.getLogger(null).info("Loaded command {}", c.getClass().getName())).collect(Collectors.toSet());
		this.commands.forEach(command -> command.getCommandStrings().forEach(cmd -> counts.put(cmd, counts.getOrDefault(cmd, 0) + 1)));
		final var clash = counts.keySet().stream().filter(key -> counts.get(key) > 1).collect(Collectors.joining(", "));
		if(Objects.nonNull(clash) && !clash.isEmpty()){
			Log.getLogger(null).error("Command clash: {}", clash);
		}
	}
	
	@Override
	public void onGuildMessageReceived(@Nonnull final GuildMessageReceivedEvent event){
		super.onGuildMessageReceived(event);
		try{
			if(isCommand(event.getGuild(), event.getMessage().getContentRaw())){
				Actions.deleteMessage(event.getMessage());
				final var args = new LinkedList<>(Arrays.asList(event.getMessage().getContentRaw().split(" ")));
				final var cmdText = args.pop().substring(NewSettings.getConfiguration(event.getGuild()).getPrefix().orElse(defaultPrefix).length());
				this.getCommand(cmdText).ifPresentOrElse(command -> {
					if(Objects.equals(command.getScope(), -5) || Objects.equals(command.getScope(), event.getChannel().getType().getId())){
						try{
							Log.getLogger(event.getGuild()).info("Executing command `{}`({}) from {}, args: {}", cmdText, command.getName(), event.getAuthor(), args);
							if(command.execute(event, args) == CommandResult.FAILED){
								Actions.replyPrivate(event.getGuild(), event.getAuthor(), "An error occurred");
							}
						}
						catch(final NotAllowedException e){
							Log.getLogger(event.getGuild()).error("Error executing command {} (not allowed)", command, e);
							final var builder = new EmbedBuilder();
							builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
							builder.setColor(Color.RED);
							builder.setTitle("You're not allowed to execute this command");
							Actions.reply(event, builder.build());
						}
						catch(final NotHandledException e){
							Log.getLogger(event.getGuild()).warn("Command {} isn't handled for {} ({})", command, event.getAuthor(), e.getMessage());
						}
						catch(final Exception e){
							Log.getLogger(event.getGuild()).error("Error executing command {}", command, e);
							final var builder = new EmbedBuilder();
							builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
							builder.setColor(Color.RED);
							builder.setTitle("This feature isn't yet configured");
							Actions.reply(event, builder.build());
						}
					}
					else{
						final var builder = new EmbedBuilder();
						builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
						builder.setColor(Color.ORANGE);
						builder.setTitle("You can't use this command in this kind of channel");
						Actions.reply(event, builder.build());
					}
				}, () -> {
					final var builder = new EmbedBuilder();
					builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
					builder.setColor(Color.ORANGE);
					builder.setTitle("Command not found");
					builder.addField("Command", cmdText, false);
					Actions.reply(event, builder.build());
				});
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
	private static boolean isCommand(@Nonnull final Guild guild, @Nonnull final String text){
		return text.startsWith(NewSettings.getConfiguration(guild).getPrefix().orElse(defaultPrefix));
	}
	
	/**
	 * get the command associated to this string.
	 *
	 * @param commandText The command text.
	 *
	 * @return The command or null if not found.
	 */
	@Nonnull
	private Optional<Command> getCommand(@Nonnull final String commandText){
		return this.commands.stream().filter(command -> command.getCommandStrings().contains(commandText.toLowerCase())).findFirst();
	}
}
