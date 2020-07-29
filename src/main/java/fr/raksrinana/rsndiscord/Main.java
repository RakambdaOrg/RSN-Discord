package fr.raksrinana.rsndiscord;

import fr.raksrinana.rsndiscord.listeners.*;
import fr.raksrinana.rsndiscord.listeners.reply.ReplyMessageListener;
import fr.raksrinana.rsndiscord.runners.ScheduledRunner;
import fr.raksrinana.rsndiscord.runners.ScheduledRunnerRunnable;
import fr.raksrinana.rsndiscord.runners.anilist.AniListActivityScheduledRunner;
import fr.raksrinana.rsndiscord.settings.GuildConfiguration;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.birthday.Birthday;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.irc.twitch.TwitchIRC;
import fr.raksrinana.rsndiscord.utils.log.Log;
import fr.raksrinana.rsndiscord.utils.music.RSNAudioManager;
import fr.raksrinana.rsndiscord.utils.reaction.ReactionUtils;
import fr.raksrinana.rsndiscord.utils.schedule.ScheduleUtils;
import fr.raksrinana.rsndiscord.utils.trakt.TraktUtils;
import fr.raksrinana.utils.http.JacksonObjectMapper;
import kong.unirest.Unirest;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import picocli.CommandLine;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

public class Main{
	public static final ZonedDateTime bootTime = ZonedDateTime.now();
	public static final boolean DEVELOPMENT = Boolean.parseBoolean(System.getProperty("rsndev", "false"));
	@Getter
	private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);
	@Getter
	private static CLIParameters parameters;
	@Getter
	private static JDA jda;
	private static ConsoleHandler consoleHandler;
	
	/**
	 * Main entry point.
	 *
	 * @param args Not used.
	 */
	public static void main(@NonNull final String[] args){
		parameters = Optional.ofNullable(loadEnv(args)).orElseThrow(() -> new IllegalStateException("Failed to load environment"));
		Unirest.config().setObjectMapper(new JacksonObjectMapper()).connectTimeout(30000).socketTimeout(30000).enableCookieManagement(true).verifySsl(true);
		try{
			Log.getLogger(null).info("Building JDA");
			final var jdaBuilder = JDABuilder.createDefault(System.getProperty("RSN_TOKEN"))
					.enableIntents(GatewayIntent.GUILD_MEMBERS)
					.setMemberCachePolicy(MemberCachePolicy.ALL);
			jdaBuilder.addEventListeners(new CommandsMessageListener());
			jdaBuilder.addEventListeners(new ShutdownListener());
			jdaBuilder.addEventListeners(new LogListener());
			jdaBuilder.addEventListeners(new AutoRolesListener());
			jdaBuilder.addEventListeners(new AutoReactionsChannelMessageListener());
			jdaBuilder.addEventListeners(new ReactionListener());
			jdaBuilder.addEventListeners(new ReplyMessageListener());
			jdaBuilder.addEventListeners(new OnlyMediaChannelListener());
			jdaBuilder.setAutoReconnect(true);
			jda = jdaBuilder.build();
			jda.awaitReady();
			jda.getPresence().setActivity(Activity.playing(CommandsMessageListener.defaultPrefix + "help for the help"));
			Log.getLogger(null).info("Loaded {} guild settings", jda.getGuilds().stream().map(Settings::get).count());
			Log.getLogger(null).info("Adding handlers");
			ReactionUtils.registerAllHandlers();
			ScheduleUtils.registerAllHandlers();
			Log.getLogger(null).info("Creating runners");
			registerAllScheduledRunners(jda);
			Log.getLogger(null).info("Started");
			announceStart();
			restartTwitchIRCConnections();
			jda.getGuilds().forEach(guild -> {
				var settings = Settings.get(guild);
				var b = settings.getBirthdays();
				b.getDates().forEach((user, date) -> b.getBirthdays().put(user, new Birthday(date)));
				b.getDates().clear();
			});
		}
		catch(final LoginException | InterruptedException e){
			Log.getLogger(null).error("Couldn't start bot", e);
			System.exit(1);
		}
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			Log.getLogger(null).info("Shutdown hook triggered");
			Settings.close();
		}));
		Log.getLogger(null).info("Shutdown hook registered");
		consoleHandler = new ConsoleHandler();
		consoleHandler.start();
	}
	
	static CLIParameters loadEnv(@NonNull String[] args){
		Log.getLogger(null).info("Starting bot version {}", getRSNBotVersion());
		if(DEVELOPMENT){
			Log.getLogger(null).warn("Developer mode activated, shouldn't be used in production!");
		}
		
		final var parameters = new CLIParameters();
		var cli = new CommandLine(parameters);
		cli.registerConverter(Path.class, Paths::get);
		cli.setStopAtUnmatched(true);
		try{
			cli.parseArgs(args);
		}
		catch(final CommandLine.ParameterException e){
			Log.getLogger(null).error("Failed to parse arguments", e);
			return null;
		}
		
		final var prop = new Properties();
		try(final var is = Files.newInputStream(parameters.getConfigurationFile())){
			prop.load(is);
		}
		catch(final IOException e){
			Log.getLogger(null).warn("Failed to read file {}", parameters.getConfigurationFile());
		}
		prop.forEach((key, value) -> System.setProperty(key.toString(), value.toString()));
		Log.getLogger(null).debug("Loaded {} properties from file", prop.keySet().size());
		return parameters;
	}
	
	private static void registerAllScheduledRunners(@NonNull JDA jda){
		Utilities.getAllInstancesOf(ScheduledRunner.class, Main.class.getPackage().getName() + ".runners", c -> {
			try{
				if(!c.equals(AniListActivityScheduledRunner.class)){
					return c.getConstructor(JDA.class).newInstance(jda);
				}
			}
			catch(InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e){
				Log.getLogger(null).error("Failed to create instance of {}", c.getName(), e);
			}
			return null;
		}).stream().peek(c -> Log.getLogger(null).info("Loaded scheduled runner {}", c.getClass().getName())).forEach(scheduledRunner -> executorService.scheduleAtFixedRate(new ScheduledRunnerRunnable(scheduledRunner), scheduledRunner.getDelay(), scheduledRunner.getPeriod(), scheduledRunner.getPeriodUnit()));
	}
	
	/**
	 * Announce the bot started in channels defined in the configuration.
	 *
	 * @see GuildConfiguration#getAnnounceStartChannel()
	 */
	private static void announceStart(){
		Main.jda.getGuilds().stream().map(Settings::get).map(GuildConfiguration::getAnnounceStartChannel).flatMap(Optional::stream).map(ChannelConfiguration::getChannel).flatMap(Optional::stream).forEach(channel -> Actions.sendMessage(channel, translate(channel.getGuild(), "started"), null));
	}
	
	/**
	 * Connects to IRC channels defined in the configuration.
	 *
	 * @see fr.raksrinana.rsndiscord.settings.guild.TwitchConfiguration#getTwitchAutoConnectUsers()
	 */
	private static void restartTwitchIRCConnections(){
		Main.getJda().getGuilds().forEach(guild -> Settings.get(guild).getTwitchConfiguration().getTwitchAutoConnectUsers().forEach(user -> {
			try{
				TwitchIRC.connect(guild, user);
			}
			catch(Exception e){
				Log.getLogger(guild).error("Failed to automatically connect to twitch user {}", user, e);
			}
		}));
	}
	
	/**
	 * Get the version of the bot.
	 *
	 * @return The version, or {@code "Unknown"} if unknown.
	 */
	@NonNull
	public static String getRSNBotVersion(){
		final var properties = new Properties();
		try{
			properties.load(Main.class.getResource("/version.properties").openStream());
		}
		catch(final Exception e){
			Log.getLogger(null).warn("Error reading version", e);
		}
		return properties.getProperty("bot.version", "Unknown");
	}
	
	/**
	 * Close the bot.
	 */
	public static void close(){
		TraktUtils.stopAll();
		ReplyMessageListener.stopAll();
		RSNAudioManager.stopAll();
		TwitchIRC.close();
		executorService.shutdownNow();
		consoleHandler.close();
		Settings.close();
		Main.getJda().shutdown();
		final var client = Main.getJda().getHttpClient();
		client.connectionPool().evictAll();
		client.dispatcher().executorService().shutdown();
	}
}
