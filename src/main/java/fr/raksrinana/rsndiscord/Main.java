package fr.raksrinana.rsndiscord;

import fr.raksrinana.rsndiscord.api.twitch.TwitchUtils;
import fr.raksrinana.rsndiscord.api.twitter.TwitterApi;
import fr.raksrinana.rsndiscord.command.SlashCommandService;
import fr.raksrinana.rsndiscord.event.EventListener;
import fr.raksrinana.rsndiscord.music.RSNAudioManager;
import fr.raksrinana.rsndiscord.reaction.ReactionUtils;
import fr.raksrinana.rsndiscord.reply.UserReplyEventListener;
import fr.raksrinana.rsndiscord.runner.RunnerUtils;
import fr.raksrinana.rsndiscord.settings.impl.GuildConfiguration;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import fr.raksrinana.rsndiscord.utils.json.JacksonObjectMapper;
import kong.unirest.Unirest;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;
import picocli.CommandLine;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static net.dv8tion.jda.api.OnlineStatus.ONLINE;

@Log4j2
public class Main{
	public static final ZonedDateTime bootTime = ZonedDateTime.now();
	public static final boolean DEVELOPMENT = Boolean.parseBoolean(System.getProperty("rsndev", "false"));
	@Getter
	private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);
	@Getter
	private static CLIParameters parameters;
	@Getter
	private static JDA jda;
	
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
				.enableCookieManagement(true)
				.verifySsl(true);
		
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			log.info("Shutdown hook triggered");
			Settings.close();
		}));
		log.info("Shutdown hook registered");
		
		try{
			log.info("Building JDA");
			var jdaBuilder = JDABuilder.createDefault(System.getProperty("RSN_TOKEN"))
					.enableIntents(GatewayIntent.GUILD_MEMBERS)
					.setMemberCachePolicy(MemberCachePolicy.ALL)
					.enableCache(CacheFlag.EMOTE)
					.setAutoReconnect(true);
			
			registerAllEventListeners(jdaBuilder);
			
			jda = jdaBuilder.build();
			jda.awaitReady();
			log.info("Loaded {} guild settings", jda.getGuilds().stream().map(Settings::get).count());
			
			JDAWrappers.editPresence()
					.setStatus(ONLINE)
					.setActivity(Activity.of(Activity.ActivityType.DEFAULT, "Bip bip"));
			
			SlashCommandService.registerGlobalCommands();
			SlashCommandService.registerGuildCommands(jda.getGuildById(735921627631583394L));
			ReactionUtils.registerAllHandlers();
			RunnerUtils.registerAllScheduledRunners();
			
			log.info("Started");
			announceStart();
			
			executorService.schedule((Runnable) TwitchUtils::connect, 15, TimeUnit.SECONDS);
		}
		catch(LoginException | InterruptedException e){
			log.error("Couldn't start bot", e);
			new ForceShutdownThread().start();
			close();
		}
		catch(Exception e){
			log.error("Bot error", e);
			new ForceShutdownThread().start();
			close();
		}
	}
	
	@NotNull
	static CLIParameters loadEnv(@NotNull String[] args){
		log.info("Starting bot");
		if(DEVELOPMENT){
			log.warn("Developer mode activated, shouldn't be used in production!");
		}
		
		var parameters = new CLIParameters();
		var cli = new CommandLine(parameters);
		cli.registerConverter(Path.class, Paths::get);
		cli.setUnmatchedArgumentsAllowed(true);
		try{
			cli.parseArgs(args);
		}
		catch(CommandLine.ParameterException e){
			log.error("Failed to parse arguments", e);
			cli.usage(System.out);
			throw new IllegalStateException("Failed to load environment");
		}
		
		var prop = new Properties();
		try(var is = Files.newInputStream(parameters.getConfigurationFile())){
			prop.load(is);
		}
		catch(IOException e){
			log.warn("Failed to read file {}", parameters.getConfigurationFile());
		}
		prop.forEach((key, value) -> System.setProperty(key.toString(), value.toString()));
		log.debug("Loaded {} properties from file", prop.keySet().size());
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
				.forEach(channel -> JDAWrappers.message(channel, translate(channel.getGuild(), "started")).submit());
	}
	
	/**
	 * Close the bot.
	 */
	public static void close(){
		TwitterApi.removeStreamFilters();
		UserReplyEventListener.stopAll();
		RSNAudioManager.stopAll();
		TwitchUtils.close();
		executorService.shutdownNow();
		Settings.close();
		Main.getJda().shutdown();
		
		var client = Main.getJda().getHttpClient();
		client.connectionPool().evictAll();
		client.dispatcher().executorService().shutdown();
	}
}
