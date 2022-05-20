package fr.raksrinana.rsndiscord.utils;

import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.RestAction;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import java.awt.Color;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Log4j2
public class Utilities{
	public static final long MAIN_RAKSRINANA_ACCOUNT = 170119951498084352L;
	public static final long SECOND_RAKSRINANA_ACCOUNT = 432628353024131085L;
	public static final Set<Long> RAKSRINANA_ACCOUNTS = Set.of(MAIN_RAKSRINANA_ACCOUNT, SECOND_RAKSRINANA_ACCOUNT);
	public static final DateTimeFormatter DATE_TIME_MINUTE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z");
	private static final Pattern PERIOD_PATTERN = Pattern.compile("(\\d+)([mhd])");
	
	@FunctionalInterface
	public interface ClassInstantiator<T>{
		@NotNull
		T apply(@NotNull Class<?> clazz) throws
				InstantiationException,
				NoSuchMethodException,
				InvocationTargetException,
				IllegalAccessException;
	}
	
	/**
	 * Tell if the member is a moderator.
	 *
	 * @param member The member to test.
	 *
	 * @return True if moderator, false otherwise.
	 */
	public static boolean isModerator(@NotNull Member member){
		return isAdmin(member) || Settings.get(member.getGuild()).getModeratorRoles().stream()
				.map(RoleConfiguration::getRole)
				.flatMap(Optional::stream)
				.anyMatch(role -> member.getRoles().contains(role));
	}
	
	/**
	 * Tell if the member is an admin.
	 *
	 * @param member The member to test.
	 *
	 * @return True if admin, false otherwise.
	 */
	public static boolean isAdmin(@NotNull Member member){
		return isCreator(member) || member.getRoles().stream()
				.anyMatch(role -> role.hasPermission(Permission.ADMINISTRATOR));
	}
	
	/**
	 * Tell if a user is this bot creator.
	 *
	 * @param member The member to test.
	 *
	 * @return True if the creator, false otherwise.
	 */
	public static boolean isCreator(@NotNull Member member){
		return isCreator(member.getUser());
	}
	
	/**
	 * Tell if a user is this bot creator.
	 *
	 * @param user The user to test.
	 *
	 * @return True if the creator, false otherwise.
	 */
	public static boolean isCreator(@NotNull User user){
		return RAKSRINANA_ACCOUNTS.stream()
				.anyMatch(id -> Objects.equals(user.getIdLong(), id));
	}
	
	@NotNull
	public static String durationToString(@NotNull Duration duration){
		if(duration.toDaysPart() > 0){
			return String.format("%dd %dh%02dm%02ds", duration.toDaysPart(), duration.toHoursPart(), duration.toMinutesPart(), duration.toSecondsPart());
		}
		if(duration.toHoursPart() > 0){
			return String.format("%dh%02dm%02ds", duration.toHoursPart(), duration.toMinutesPart(), duration.toSecondsPart());
		}
		if(duration.toMinutesPart() > 0){
			return String.format("%02dm%02ds", duration.toMinutesPart(), duration.toSecondsPart());
		}
		return String.format("%02ds", duration.toSecondsPart());
	}
	
	@NotNull
	public static Duration parseDuration(@NotNull String period){
		period = period.toLowerCase(Locale.ENGLISH);
		var matcher = PERIOD_PATTERN.matcher(period);
		var duration = Duration.ZERO;
		
		while(matcher.find()){
			int amount = Integer.parseInt(matcher.group(1));
			
			duration = duration.plus(switch(matcher.group(2)){
				case "m" -> duration.plus(Duration.ofMinutes(amount));
				case "h" -> duration.plus(Duration.ofHours(amount));
				case "d" -> duration.plus(Duration.ofDays(amount));
				default -> Duration.ZERO;
			});
		}
		return duration;
	}
	
	/**
	 * Report an exception by sending a private message to the creator.
	 *
	 * @param throwable The exception to send.
	 *
	 * @return A completable future of a message (see {@link RestAction#submit()}).
	 */
	@NotNull
	public static CompletableFuture<Message> reportException(@NotNull String message, @NotNull Throwable throwable){
		return Optional.ofNullable(Main.getJda().getUserById(MAIN_RAKSRINANA_ACCOUNT))
				.map(user -> user.openPrivateChannel().submit()
						.thenCompose(privateChannel -> JDAWrappers.message(privateChannel, MessageFormat.format("RSN got an exception: {0}\n", message))
								.embed(throwableToEmbed(throwable).build())
								.submit()))
				.orElse(CompletableFuture.failedFuture(new RuntimeException("User not found")));
	}
	
	@NotNull
	private static EmbedBuilder throwableToEmbed(@NotNull Throwable throwable){
		var embed = new EmbedBuilder();
		embed.setTitle(throwable.getMessage().substring(0, Math.min(throwable.getMessage().length(), MessageEmbed.TITLE_MAX_LENGTH)));
		embed.setColor(Color.RED);
		var trace = ExceptionUtils.getStackTrace(throwable);
		embed.addField("Trace", trace.substring(0, Math.min(trace.length(), MessageEmbed.VALUE_MAX_LENGTH)), false);
		return embed;
	}
	
	@NotNull
	public static <T> Stream<T> getAllAnnotatedWith(@NotNull Class<? extends Annotation> annotationClazz, @NotNull ClassInstantiator<T> instantiator){
		return new Reflections(Main.class.getPackage().getName())
				.getTypesAnnotatedWith(annotationClazz).stream()
				.map(clazz -> {
					try{
						return instantiator.apply(clazz);
					}
					catch(InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e){
						log.error("Failed to create instance of {}", annotationClazz.getName(), e);
					}
					return null;
				})
				.filter(Objects::nonNull);
	}
	
	public static boolean containsChannel(@NotNull Collection<ChannelConfiguration> channels, @NotNull MessageChannel channel){
		return channels.stream().anyMatch(c -> Objects.equals(c.getChannelId(), channel.getIdLong()));
	}
}
