package fr.raksrinana.rsndiscord;

import fr.raksrinana.rsndiscord.api.irc.twitch.TwitchIRC;
import fr.raksrinana.rsndiscord.api.trakt.TraktApi;
import fr.raksrinana.rsndiscord.event.EventListener;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.music.RSNAudioManager;
import fr.raksrinana.rsndiscord.reaction.ReactionUtils;
import fr.raksrinana.rsndiscord.reply.UserReplyEventListener;
import fr.raksrinana.rsndiscord.runner.RunnerUtils;
import fr.raksrinana.rsndiscord.schedule.ScheduleUtils;
import fr.raksrinana.rsndiscord.settings.GuildConfiguration;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.TwitchConfiguration;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.utils.JacksonObjectMapper;
import fr.raksrinana.rsndiscord.utils.Utilities;
import kong.unirest.Unirest;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.jetbrains.annotations.NotNull;
import picocli.CommandLine;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import static fr.raksrinana.rsndiscord.event.CommandsEventListener.DEFAULT_PREFIX;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static net.dv8tion.jda.api.OnlineStatus.ONLINE;

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
	public static void main(@NotNull String[] args){
		parameters = loadEnv(args);
		Unirest.config()
				.setObjectMapper(new JacksonObjectMapper())
				.connectTimeout(30000)
				.socketTimeout(30000)
				.enableCookieManagement(true)
				.verifySsl(true);
		consoleHandler = new ConsoleHandler();
		try{
			Log.getLogger(null).info("Building JDA");
			var jdaBuilder = JDABuilder.createDefault(System.getProperty("RSN_TOKEN"))
					.enableIntents(GatewayIntent.GUILD_MEMBERS)
					.setMemberCachePolicy(MemberCachePolicy.ALL)
					.setAutoReconnect(true);
			registerAllEventListeners(jdaBuilder);
			jda = jdaBuilder.build();
			jda.awaitReady();
			jda.getPresence().setPresence(ONLINE, Activity.of(Activity.ActivityType.DEFAULT, DEFAULT_PREFIX + "help for the help"));
			Log.getLogger(null).info("Loaded {} guild settings", jda.getGuilds().stream().map(Settings::get).count());
			Log.getLogger(null).info("Adding handlers");
			ReactionUtils.registerAllHandlers();
			ScheduleUtils.registerAllHandlers();
			Log.getLogger(null).info("Creating runners");
			RunnerUtils.registerAllScheduledRunners();
			Log.getLogger(null).info("Started");
			announceStart();
			restartTwitchIRCConnections();
			
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				Log.getLogger(null).info("Shutdown hook triggered");
				Settings.close();
			}));
			Log.getLogger(null).info("Shutdown hook registered");
			consoleHandler.start();
		}
		catch(LoginException | InterruptedException e){
			Log.getLogger(null).error("Couldn't start bot", e);
			close();
		}
		catch(Exception e){
			Log.getLogger(null).error("Bot error", e);
			close();
		}
	}
	
	@NotNull
	static CLIParameters loadEnv(@NotNull String[] args){
		Log.getLogger(null).info("Starting bot version {}", getRSNBotVersion());
		if(DEVELOPMENT){
			Log.getLogger(null).warn("Developer mode activated, shouldn't be used in production!");
		}
		
		var parameters = new CLIParameters();
		var cli = new CommandLine(parameters);
		cli.registerConverter(Path.class, Paths::get);
		cli.setUnmatchedArgumentsAllowed(true);
		try{
			cli.parseArgs(args);
		}
		catch(CommandLine.ParameterException e){
			Log.getLogger(null).error("Failed to parse arguments", e);
			cli.usage(System.out);
			throw new IllegalStateException("Failed to load environment");
		}
		
		var prop = new Properties();
		try(var is = Files.newInputStream(parameters.getConfigurationFile())){
			prop.load(is);
		}
		catch(IOException e){
			Log.getLogger(null).warn("Failed to read file {}", parameters.getConfigurationFile());
		}
		prop.forEach((key, value) -> System.setProperty(key.toString(), value.toString()));
		Log.getLogger(null).debug("Loaded {} properties from file", prop.keySet().size());
		return parameters;
	}
	
	private static void registerAllEventListeners(@NotNull JDABuilder jdaBuilder){
		Utilities.getAllAnnotatedWith(EventListener.class, clazz -> (ListenerAdapter) clazz.getConstructor().newInstance())
				.forEach(jdaBuilder::addEventListeners);
	}
	
	/**
	 * Announce the bot started in channels defined in the configuration.
	 *
	 * @see GuildConfiguration#getAnnounceStartChannel()
	 */
	private static void announceStart(){
		Main.jda.getGuilds().stream()
				.map(Settings::get)
				.map(GuildConfiguration::getAnnounceStartChannel)
				.flatMap(Optional::stream)
				.map(ChannelConfiguration::getChannel)
				.flatMap(Optional::stream)
				.forEach(channel -> channel.sendMessage(translate(channel.getGuild(), "started")).submit());
	}
	
	/**
	 * Connects to IRC channels defined in the configuration.
	 *
	 * @see TwitchConfiguration#getTwitchAutoConnectUsers()
	 */
	private static void restartTwitchIRCConnections(){
		Main.getJda().getGuilds()
				.forEach(guild -> Settings.get(guild)
						.getTwitchConfiguration()
						.getTwitchAutoConnectUsers()
						.forEach(user -> {
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
	@NotNull
	public static String getRSNBotVersion(){
		var properties = new Properties();
		try{
			var versionProperties = Main.class.getResource("/version.properties");
			if(Objects.nonNull(versionProperties)){
				try(var is = versionProperties.openStream()){
					properties.load(is);
				}
			}
		}
		catch(Exception e){
			Log.getLogger(null).warn("Error reading version", e);
		}
		return properties.getProperty("bot.version", "Unknown");
	}
	
	/**
	 * Close the bot.
	 */
	public static void close(){
		TraktApi.stopAll();
		UserReplyEventListener.stopAll();
		RSNAudioManager.stopAll();
		TwitchIRC.close();
		executorService.shutdownNow();
		consoleHandler.close();
		Settings.close();
		Main.getJda().shutdown();
		
		var client = Main.getJda().getHttpClient();
		client.connectionPool().evictAll();
		client.dispatcher().executorService().shutdown();
	}
}
