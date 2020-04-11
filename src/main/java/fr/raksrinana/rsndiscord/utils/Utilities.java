package fr.raksrinana.rsndiscord.utils;

import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.RestAction;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.reflections.Reflections;
import java.awt.Color;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Utilities{
	public static final long RAKSRINANA_ACCOUNT = 170119951498084352L;
	private static final long LOPINETTE_ACCOUNT = 432628353024131085L;
	public static final DateTimeFormatter DATE_TIME_MINUTE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z");
	
	/**
	 * Tell if a member is part of the team (admin or moderator).
	 *
	 * @param member The member to test.
	 *
	 * @return True if part of the team, false otherwise.
	 */
	public static boolean isTeam(@NonNull final Member member){
		return isModerator(member) || isAdmin(member);
	}
	
	/**
	 * Tell if the member is a moderator.
	 *
	 * @param member The member to test.
	 *
	 * @return True if moderator, false otherwise.
	 */
	public static boolean isModerator(@NonNull final Member member){
		return isAdmin(member) || Settings.get(member.getGuild()).getModeratorRoles().stream().map(RoleConfiguration::getRole).flatMap(Optional::stream).anyMatch(role -> member.getRoles().contains(role));
	}
	
	/**
	 * Tell if the member is an admin.
	 *
	 * @param member The member to test.
	 *
	 * @return True if admin, false otherwise.
	 */
	public static boolean isAdmin(@NonNull final Member member){
		return member.getRoles().stream().anyMatch(role -> role.hasPermission(Permission.ADMINISTRATOR)) || isCreator(member.getUser());
	}
	
	/**
	 * Tell if a user is this bot created.
	 *
	 * @param user The member to test.
	 *
	 * @return True if the creator, false otherwise.
	 */
	public static boolean isCreator(@NonNull final User user){
		return Objects.equals(user.getIdLong(), RAKSRINANA_ACCOUNT) || Objects.equals(user.getIdLong(), LOPINETTE_ACCOUNT);
	}
	
	/**
	 * Copy an embed message.
	 *
	 * @param embed The embed message to copy.
	 *
	 * @return An embed builder based on the given embed.
	 */
	@NonNull
	public static EmbedBuilder copyEmbed(final MessageEmbed embed){
		final var builder = buildEmbed(null, embed.getColor(), embed.getTitle(), null);
		if(Objects.nonNull(embed.getAuthor())){
			builder.setAuthor(embed.getAuthor().getName(), embed.getAuthor().getUrl(), embed.getAuthor().getIconUrl());
		}
		builder.setDescription(embed.getDescription());
		embed.getFields().forEach(builder::addField);
		return builder;
	}
	
	/**
	 * Build a basic embed with an author, title and color.
	 *
	 * @param author   The author (if none, set to {@code null}).
	 * @param color    The color (if use default, set to {@code null}).
	 * @param title    The title.
	 * @param titleURL The url of the title (if none, set to {@code null}).
	 *
	 * @return An embed builder based on the parameters.
	 *
	 * @see EmbedBuilder
	 */
	@NonNull
	public static EmbedBuilder buildEmbed(final User author, final Color color, final String title, final String titleURL){
		final var builder = new EmbedBuilder();
		if(Objects.nonNull(author)){
			builder.setAuthor(author.getName(), null, author.getAvatarUrl());
		}
		if(Objects.nonNull(title) && !title.isBlank()){
			builder.setTitle(title, titleURL);
		}
		builder.setColor(color);
		return builder;
	}
	
	@NonNull
	public static String durationToString(final Duration duration){
		return String.format("%dd %dh%02dm%02ds", duration.toDaysPart(), duration.toHoursPart(), duration.toMinutesPart(), duration.toSecondsPart());
	}
	
	/**
	 * Report an exception by sending a private message to the creator.
	 *
	 * @param throwable The exception to send.
	 *
	 * @return A completable future of a message (see {@link RestAction#submit()}).
	 *
	 * @see Actions#sendPrivateMessage(Guild, PrivateChannel, CharSequence, MessageEmbed)
	 */
	@NonNull
	public static CompletableFuture<Message> reportException(@NonNull Throwable throwable){
		return Actions.sendPrivateMessage(RAKSRINANA_ACCOUNT, MessageFormat.format("RSN got an exception: {0}\n{1}", ExceptionUtils.getMessage(throwable), ExceptionUtils.getStackTrace(throwable)), null);
	}
	
	/**
	 * Get a message by its id.
	 *
	 * @param channel   The channel to search the message in.
	 * @param messageId The id of the message.
	 *
	 * @return An completable future of an optional message (see {@link RestAction#submit()}).
	 */
	public static CompletableFuture<Message> getMessageById(@NonNull TextChannel channel, long messageId){
		return channel.retrieveMessageById(messageId).submit();
	}
	
	public static <T> Collection<? extends T> getAllInstancesOf(Class<T> klass, @NonNull String packageName, Function<Class<? extends T>, ? extends T> createInstance){
		return new Reflections(packageName).getSubTypesOf(klass).stream().filter(c -> !c.isInterface()).sorted(Comparator.comparing(Class::getCanonicalName)).map(createInstance).filter(Objects::nonNull).map(klass::cast).collect(Collectors.toList());
	}
	
	public static EmbedBuilder throwableToEmbed(Throwable throwable){
		final var embed = new EmbedBuilder();
		embed.setTitle(throwable.getMessage().substring(0, Math.min(throwable.getMessage().length(), MessageEmbed.TITLE_MAX_LENGTH)));
		embed.setColor(Color.RED);
		final var trace = ExceptionUtils.getStackTrace(throwable);
		embed.addField("Trace", trace.substring(0, Math.min(trace.length(), MessageEmbed.VALUE_MAX_LENGTH)), false);
		return embed;
	}
	
	public static Optional<Role> getRoleById(long id){
		return Optional.ofNullable(Main.getJda().getRoleById(id));
	}
}
