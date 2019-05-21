package fr.mrcraftcod.gunterdiscord.listeners;

import fr.mrcraftcod.gunterdiscord.commands.*;
import fr.mrcraftcod.gunterdiscord.commands.anilist.AniListCommandComposite;
import fr.mrcraftcod.gunterdiscord.commands.config.ConfigurationCommandComposite;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.commands.generic.NotAllowedException;
import fr.mrcraftcod.gunterdiscord.commands.luxbus.LuxBusCommandComposite;
import fr.mrcraftcod.gunterdiscord.commands.music.MusicCommandComposite;
import fr.mrcraftcod.gunterdiscord.commands.photo.PhotoCommandComposite;
import fr.mrcraftcod.gunterdiscord.commands.quiz.QuizCommandComposite;
import fr.mrcraftcod.gunterdiscord.commands.twitch.TwitchCommandComposite;
import fr.mrcraftcod.gunterdiscord.commands.warn.CustomWarnCommand;
import fr.mrcraftcod.gunterdiscord.commands.warn.DoubleWarnCommand;
import fr.mrcraftcod.gunterdiscord.commands.warn.MegaWarnCommand;
import fr.mrcraftcod.gunterdiscord.commands.warn.NormalWarnCommand;
import fr.mrcraftcod.gunterdiscord.settings.configs.PrefixConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;
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
			new YoutubeCommand(),
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
			new LuxBusCommandComposite()
	};
	private final static String defaultPrefix = "g?";
	
	/**
	 * Constructor.
	 */
	public CommandsMessageListener(){
		final var counts = new HashMap<String, Integer>();
		Arrays.asList(commands).forEach(command -> command.getCommand().forEach(cmd -> counts.put(cmd, counts.getOrDefault(cmd, 0) + 1)));
		final var clash = counts.keySet().stream().filter(key -> counts.get(key) > 1).collect(Collectors.joining(", "));
		if(Objects.nonNull(clash) && !clash.isEmpty()){
			getLogger(null).error("Command clash: {}", clash);
		}
	}
	
	@Override
	public void onGuildMessageReceived(@NotNull final GuildMessageReceivedEvent event){
		super.onGuildMessageReceived(event);
		try{
			if(isCommand(event.getGuild(), event.getMessage().getContentRaw())){
				Actions.deleteMessage(event.getMessage());
				final var args = new LinkedList<>(Arrays.asList(event.getMessage().getContentRaw().split(" ")));
				final var cmdText = args.pop().substring(new PrefixConfig(event.getGuild()).getObject(defaultPrefix).length());
				final var command = getCommand(cmdText);
				if(Objects.nonNull(command)){
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
				}
				else{
					final var builder = new EmbedBuilder();
					builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
					builder.setColor(Color.ORANGE);
					builder.setTitle("Command not found");
					builder.addField("Command", cmdText, false);
					Actions.reply(event, builder.build());
				}
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
	private static boolean isCommand(final Guild guild, final String text){
		return text.startsWith(new PrefixConfig(guild).getObject(defaultPrefix));
	}
	
	/**
	 * get the command associated to this string.
	 *
	 * @param commandText The command text.
	 *
	 * @return The command or null if not found.
	 */
	private static Command getCommand(final String commandText){
		return Arrays.stream(commands).filter(command -> command.getCommand().contains(commandText.toLowerCase())).findFirst().orElse(null);
	}
}
