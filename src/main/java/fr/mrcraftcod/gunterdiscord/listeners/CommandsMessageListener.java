package fr.mrcraftcod.gunterdiscord.listeners;

import fr.mrcraftcod.gunterdiscord.commands.*;
import fr.mrcraftcod.gunterdiscord.commands.anilist.AniListCommandComposite;
import fr.mrcraftcod.gunterdiscord.commands.config.ConfigurationCommandComposite;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.commands.generic.NotAllowedException;
import fr.mrcraftcod.gunterdiscord.commands.generic.NotHandledException;
import fr.mrcraftcod.gunterdiscord.commands.luxbus.LuxBusGetStopCommand;
import fr.mrcraftcod.gunterdiscord.commands.music.MusicCommandComposite;
import fr.mrcraftcod.gunterdiscord.commands.overwatch.OverwatchCommandComposite;
import fr.mrcraftcod.gunterdiscord.commands.photo.PhotoCommandComposite;
import fr.mrcraftcod.gunterdiscord.commands.quiz.QuizCommandComposite;
import fr.mrcraftcod.gunterdiscord.commands.stopwatch.StopwatchCommand;
import fr.mrcraftcod.gunterdiscord.commands.twitch.TwitchCommandComposite;
import fr.mrcraftcod.gunterdiscord.commands.warn.CustomWarnCommand;
import fr.mrcraftcod.gunterdiscord.commands.warn.DoubleWarnCommand;
import fr.mrcraftcod.gunterdiscord.commands.warn.MegaWarnCommand;
import fr.mrcraftcod.gunterdiscord.commands.warn.NormalWarnCommand;
import fr.mrcraftcod.gunterdiscord.settings.NewSettings;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import javax.annotation.Nonnull;
import java.awt.Color;
import java.util.*;
import java.util.stream.Collectors;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 09/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-09
 */
public class CommandsMessageListener extends ListenerAdapter{
	public static final Command[] commands = {
			new PhotoCommandComposite(),
			new QuizCommandComposite(),
			new ReportCommand(),
			new ConfigurationCommandComposite(),
			new HelpCommand(),
			new AvatarCommand(),
			new NicknameCommand(),
			new SayCommand(),
			new QuestionCommand(),
			new AnnoyCommand(),
			new InfosCommand(),
			new NormalWarnCommand(),
			new DoubleWarnCommand(),
			new MegaWarnCommand(),
			new CustomWarnCommand(),
			new MusicCommandComposite(),
			new StopCommand(),
			new TimeCommand(),
			new WarnInfoCommand(),
			new AniListCommandComposite(),
			new EmotesCommand(),
			new TempParticipationCommand(),
			new DogCommand(),
			new TwitchCommandComposite(),
			new LuxBusGetStopCommand(),
			new OverwatchCommandComposite(),
			new StopwatchCommand(),
			new PoopCommand()
	};
	public final static String defaultPrefix = System.getProperty("RSN_DEFAULT_PREFIX", "g?");
	
	/**
	 * Constructor.
	 */
	public CommandsMessageListener(){
		final var counts = new HashMap<String, Integer>();
		Arrays.asList(commands).forEach(command -> command.getCommandStrings().forEach(cmd -> counts.put(cmd, counts.getOrDefault(cmd, 0) + 1)));
		final var clash = counts.keySet().stream().filter(key -> counts.get(key) > 1).collect(Collectors.joining(", "));
		if(Objects.nonNull(clash) && !clash.isEmpty()){
			getLogger(null).error("Command clash: {}", clash);
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
				getCommand(cmdText).ifPresentOrElse(command -> {
					if(Objects.equals(command.getScope(), -5) || Objects.equals(command.getScope(), event.getChannel().getType().getId())){
						try{
							getLogger(event.getGuild()).info("Executing command `{}`({}) from {}, args: {}", cmdText, command.getName(), event.getAuthor(), args);
							if(Objects.equals(command.execute(event, args), CommandResult.FAILED)){
								Actions.replyPrivate(event.getGuild(), event.getAuthor(), "An error occurred");
							}
						}
						catch(final NotAllowedException e){
							getLogger(event.getGuild()).error("Error executing command {} (not allowed)", command, e);
							final var builder = new EmbedBuilder();
							builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
							builder.setColor(Color.RED);
							builder.setTitle("You're not allowed to execute this command");
							Actions.reply(event, builder.build());
						}
						catch(final NotHandledException e){
							getLogger(event.getGuild()).warn("Command {} isn't handled for {} ({})", command, event.getAuthor(), e.getMessage());
						}
						catch(final Exception e){
							getLogger(event.getGuild()).error("Error executing command {}", command, e);
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
			getLogger(event.getGuild()).error("Error handling message", e);
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
	private static Optional<Command> getCommand(@Nonnull final String commandText){
		return Arrays.stream(commands).filter(command -> command.getCommandStrings().contains(commandText.toLowerCase())).findFirst();
	}
}
