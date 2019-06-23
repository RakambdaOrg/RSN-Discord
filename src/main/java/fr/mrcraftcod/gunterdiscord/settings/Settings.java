package fr.mrcraftcod.gunterdiscord.settings;

import fr.mrcraftcod.gunterdiscord.Main;
import fr.mrcraftcod.gunterdiscord.settings.configs.*;
import fr.mrcraftcod.gunterdiscord.settings.configurations.*;
import fr.mrcraftcod.gunterdiscord.utils.log.Log;
import net.dv8tion.jda.api.entities.Guild;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 09/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-09
 */
@SuppressWarnings("DuplicatedCode")
public class Settings{
	private static final Logger LOGGER = LoggerFactory.getLogger(Settings.class);
	public static final Configuration[] SETTINGS = new Configuration[]{
			new ModoRolesConfig(null),
			new OnlyIdeasConfig(null),
			new OnlyImagesConfig(null),
			new PrefixConfig(null),
			new ReportChannelConfig(null),
			// new PhotoChannelConfig(null),
			new QuizChannelConfig(null),
			// new PhotoConfig(null),
			new AutoRolesConfig(null),
			new NickDelayConfig(null),
			new NickLastChangeConfig(null),
			// new TrombinoscopeRoleConfig(null),
			new QuestionsChannelConfig(null),
			new QuestionsFinalChannelConfig(null),
			new YoutubeChannelConfig(null),
			new YoutubeRoleConfig(null),
			new WarnRoleConfig(null),
			new DoubleWarnRoleConfig(null),
			new MegaWarnRoleConfig(null),
			new WarnTimeConfig(null),
			new DoubleWarnTimeConfig(null),
			new MegaWarnTimeConfig(null),
			new RemoveRoleConfig(null),
			new DJRoleConfig(null),
			new NameLastChangeConfig(null),
			new EnableNameChangeLimitConfig(null),
			new AniListCodeConfig(null),
			new AniListChannelConfig(null),
			new AniListAccessTokenConfig(null),
			new AniListLastAccessConfig(null),
			new NoXPChannelsConfig(null),
			new MembersParticipationConfig(null),
			new MembersParticipationChannelConfig(null),
			new MembersParticipationPinConfig(null),
			new EmotesParticipationConfig(null),
			new EmotesParticipationChannelConfig(null),
			new EmotesParticipationPinConfig(null),
			new TwitchIRCChannelConfig(null),
			new AnilistThaChannelConfig(null),
			new AnilistThaUserConfig(null)
	};
	private static Path path;
	private static JSONObject rootSettings;
	
	/**
	 * Get the value as an object.
	 *
	 * @param guild The guild.
	 * @param name  The name of the setting.
	 *
	 * @return The object or null if not found.
	 */
	@Nonnull
	public static Optional<Object> getObject(@Nullable final Guild guild, @Nonnull final String name){
		return getServerSettings(guild).filter(settings -> settings.has(name)).map(settings -> settings.get(name));
	}
	
	/**
	 * Get the settings for the given guild.
	 *
	 * @param guild The guild.
	 *
	 * @return The settings of the guild.
	 */
	private static Optional<JSONObject> getServerSettings(@Nullable final Guild guild){
		if(Objects.isNull(guild)){
			return Optional.empty();
		}
		final JSONObject serverSettings;
		final var id = "" + guild.getIdLong();
		if(rootSettings.has(id)){
			serverSettings = rootSettings.optJSONObject(id);
		}
		else{
			serverSettings = new JSONObject();
			rootSettings.put(id, serverSettings);
		}
		return Optional.of(Objects.isNull(serverSettings) ? new JSONObject() : serverSettings);
	}
	
	/**
	 * Remove a value from a list.
	 *
	 * @param guild         The guild concerned.
	 * @param configuration The configuration.
	 * @param value         The value to remove.
	 * @param <T>           The type of the value.
	 */
	public static <T> void removeValue(@Nullable final Guild guild, @Nonnull final ListConfiguration configuration, @Nonnull final T value){
		getArray(guild, configuration.getName()).ifPresent(array -> {
			final var index = array.toList().indexOf(value);
			if(!Objects.equals(index, -1)){
				array.remove(index);
			}
		});
	}
	
	/**
	 * Get an array.
	 *
	 * @param guild The guild concerned.
	 * @param name  The name of the array.
	 *
	 * @return The array or null.
	 */
	public static Optional<JSONArray> getArray(final Guild guild, final String name){
		return getServerSettings(guild).map(settings -> settings.optJSONArray(name));
	}
	
	/**
	 * Init the settings.
	 *
	 * @param path The path of the settings to load.
	 *
	 * @throws IOException If something went wrong.
	 */
	public static void init(@Nonnull final Path path) throws IOException{
		LOGGER.info("Initializing settings");
		Settings.path = path;
		if(path.toFile().exists()){
			rootSettings = new JSONObject(String.join("", Files.readAllLines(path)));
		}
		else{
			rootSettings = new JSONObject(IOUtils.toString(Main.class.getResourceAsStream("/settings/default.json"), StandardCharsets.UTF_8));
		}
	}
	
	/**
	 * Closes the settings.
	 */
	@SuppressWarnings("EmptyMethod")
	public static void close(){
	}
	
	/**
	 * Get a setting based on its name.
	 *
	 * @param name The name of the config.
	 *
	 * @return The configuration or null if not found.
	 */
	@Nonnull
	public static Optional<Configuration> getSettings(@Nullable final String name){
		return Arrays.stream(SETTINGS).filter(configuration -> configuration.getName().equalsIgnoreCase(name)).findFirst();
	}
	
	/**
	 * Save the settings to the file.
	 *
	 * @throws IOException If something went wrong.
	 */
	public static void save() throws IOException{
		Files.write(path, Arrays.asList(rootSettings.toString(4).split("\n")), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
		Log.getLogger(null).info("Config written");
	}
	
	/**
	 * Set the value of a config.
	 *
	 * @param guild         The guild of the config.
	 * @param configuration The configuration.
	 * @param value         The value to set.
	 */
	public static void setValue(@Nullable final Guild guild, @Nonnull final ValueConfiguration configuration, @Nullable final Object value){
		getServerSettings(guild).ifPresent(settings -> settings.put(configuration.getName(), Objects.isNull(value) ? JSONObject.NULL : value));
	}
	
	/**
	 * Add a value.
	 *
	 * @param guild         The guild concerned.
	 * @param configuration The configuration.
	 * @param value         The value to add.
	 * @param <T>           The type of the value.
	 */
	public static <T> void addValue(@Nullable final Guild guild, @Nonnull final ListConfiguration configuration, @Nullable final T value){
		if(Objects.nonNull(value)){
			getServerSettings(guild).ifPresent(settings -> settings.append(configuration.getName(), value));
		}
	}
	
	/**
	 * Clears a list.
	 *
	 * @param guild         The guild concerned.
	 * @param configuration The configuration to clear.
	 */
	public static void resetList(@Nullable final Guild guild, @Nonnull final ListConfiguration configuration){
		getServerSettings(guild).ifPresent(settings -> settings.put(configuration.getName(), new JSONArray()));
	}
	
	/**
	 * Put a value inside a map.
	 *
	 * @param guild         The guild concerned.
	 * @param configuration The configuration.
	 * @param key           The key of the value to set.
	 * @param value         The value to set.
	 * @param <K>           The type of the key.
	 * @param <V>           They type of the value.
	 */
	public static <K, V> void mapValue(@Nullable final Guild guild, @Nonnull final MapConfiguration configuration, @Nonnull final K key, @Nullable final V value){
		getServerSettings(guild).ifPresent(settings -> {
			final var map = Optional.ofNullable(settings.optJSONObject(configuration.getName())).orElseGet(() -> {
				final var temp = new JSONObject();
				settings.put(configuration.getName(), temp);
				return temp;
			});
			map.put(key.toString(), Objects.isNull(value) ? JSONObject.NULL : value);
		});
	}
	
	/**
	 * Delete a key from a map.
	 *
	 * @param guild         The guild concerned.
	 * @param configuration The configuration.
	 * @param key           The key to delete.
	 * @param <K>           The type of the key.
	 */
	public static <K> void deleteKey(@Nullable final Guild guild, final MapConfiguration configuration, @Nonnull final K key){
		getServerSettings(guild).map(settings -> settings.optJSONObject(configuration.getName())).ifPresent(map -> map.remove(key.toString()));
	}
	
	/**
	 * Get an object.
	 *
	 * @param guild The guild concerned.
	 * @param name  The name of they key.
	 *
	 * @return The object or null.
	 */
	public static Optional<JSONObject> getJSONObject(@Nullable final Guild guild, @Nonnull final String name){
		return getServerSettings(guild).map(settings -> settings.optJSONObject(name));
	}
	
	/**
	 * Clears a map.
	 *
	 * @param guild         The guild concerned.
	 * @param configuration The configuration.
	 */
	public static void resetMap(@Nullable final Guild guild, @Nonnull final MapConfiguration configuration){
		getServerSettings(guild).ifPresent(settings -> settings.put(configuration.getName(), new JSONObject()));
	}
	
	/**
	 * Clears a map.
	 *
	 * @param guild         The guild concerned.
	 * @param configuration The configuration.
	 */
	public static void resetMap(@Nullable final Guild guild, @Nonnull final MapListConfiguration configuration){
		getServerSettings(guild).ifPresent(settings -> settings.put(configuration.getName(), new JSONObject()));
	}
	
	/**
	 * Add a value inside of a map of list.
	 *
	 * @param guild         The guild concerned.
	 * @param configuration The configuration.
	 * @param key           The key.
	 * @param value         The value to add.
	 * @param <K>           The type of the key.
	 * @param <V>           The type of the value.
	 */
	public static <K, V> void mapListValue(@Nullable final Guild guild, @Nonnull final MapListConfiguration configuration, @Nonnull final K key, @Nullable final V value){
		getServerSettings(guild).ifPresent(settings -> {
			final var map = Optional.ofNullable(settings.optJSONObject(configuration.getName())).orElseGet(() -> {
				final var temp = new JSONObject();
				settings.put(configuration.getName(), temp);
				return temp;
			});
			map.append(key.toString(), Objects.isNull(value) ? JSONObject.NULL : value);
		});
	}
	
	/**
	 * Delete a value inside a map of a list.
	 *
	 * @param guild         The guild concerned?
	 * @param configuration The configuration.
	 * @param key           The key.
	 * @param value         The value to remove.
	 * @param matcher       The matcher to find keys to delete.
	 * @param <K>           The type of the key.
	 * @param <V>           The type of the value.
	 */
	public static <K, V> void deleteKey(@Nullable final Guild guild, @Nonnull final MapListConfiguration<K, V> configuration, @Nonnull final K key, @Nullable final V value, final @Nonnull BiFunction<Object, V, Boolean> matcher){
		getServerSettings(guild).map(settings -> settings.optJSONObject(configuration.getName())).map(map -> map.optJSONArray(key.toString())).ifPresent(array -> IntStream.range(0, array.length()).filter(i -> matcher.apply(array.get(i), value)).findFirst().ifPresent(index -> {
			array.remove(index);
			if(array.isEmpty()){
				Settings.deleteKey(guild, configuration, key);
			}
		}));
	}
	
	/**
	 * Delete a list from of a map.
	 *
	 * @param guild         The guild concerned.
	 * @param configuration The configuration.
	 * @param key           The key.
	 * @param <K>           The type of the key.
	 * @param <V>           The type of the value.
	 */
	public static <K, V> void deleteKey(@Nullable final Guild guild, @Nonnull final MapListConfiguration<K, V> configuration, final @Nonnull K key){
		getServerSettings(guild).map(settings -> settings.optJSONObject(configuration.getName())).ifPresent(map -> map.remove(key.toString()));
	}
	
	/**
	 * Add a map inside a map.
	 *
	 * @param guild         The guild concerned.
	 * @param configuration The configuration.
	 * @param key           The key of the map.
	 * @param <K>           The type of the key.
	 */
	public static <K> void mapMapValue(@Nullable final Guild guild, @Nonnull final MapMapConfiguration configuration, @Nonnull final K key){
		getServerSettings(guild).ifPresent(settings -> {
			final var map = Optional.ofNullable(settings.optJSONObject(configuration.getName())).orElseGet(() -> {
				final var temp = new JSONObject();
				rootSettings.put(configuration.getName(), temp);
				return temp;
			});
			if(!map.has(key.toString())){
				map.put(key.toString(), new JSONObject());
			}
		});
	}
	
	/**
	 * Add a value inside a map of a map.
	 *
	 * @param guild         The guild concerned.
	 * @param configuration The configuration.
	 * @param key           The key of the first map.
	 * @param value         The key of the second map.
	 * @param insideValue   The value to set.
	 * @param <K>           The type of the first key.
	 * @param <V>           The type of the second key.
	 * @param <W>           The type of the value.
	 */
	public static <K, V, W> void mapMapValue(@Nullable final Guild guild, @Nonnull final MapMapConfiguration configuration, @Nonnull final K key, @Nonnull final V value, @Nullable final W insideValue){
		getServerSettings(guild).ifPresent(settings -> {
			final var map = Optional.ofNullable(settings.optJSONObject(configuration.getName())).orElseGet(() -> {
				final var temp = new JSONObject();
				settings.put(configuration.getName(), temp);
				return temp;
			});
			if(!map.has(key.toString())){
				map.put(key.toString(), new JSONObject());
			}
			map.optJSONObject(key.toString()).put(value.toString(), Objects.isNull(insideValue) ? JSONObject.NULL : insideValue);
		});
	}
	
	/**
	 * Delete a map containing a map.
	 *
	 * @param guild         The guild concerned.
	 * @param configuration The configuration.
	 * @param key           The key.
	 * @param <K>           The type of the key.
	 */
	public static <K> void deleteKey(@Nullable final Guild guild, @Nonnull final MapMapConfiguration configuration, @Nonnull final K key){
		getServerSettings(guild).map(settings -> settings.optJSONObject(configuration.getName())).ifPresent(map -> map.remove(key.toString()));
	}
	
	/**
	 * Reset a map of map.
	 *
	 * @param guild         The guild concerned.
	 * @param configuration The configuration.
	 */
	public static void resetMap(@Nullable final Guild guild, @Nonnull final MapMapConfiguration configuration){
		getServerSettings(guild).ifPresent(settings -> settings.put(configuration.getName(), new JSONObject()));
	}
	
	/**
	 * Delete values inside a map inside a map.
	 *
	 * @param guild         The guild concerned.
	 * @param configuration The configuration.
	 * @param key           The key.
	 * @param value         The second key.
	 * @param <K>           The type of the key.
	 * @param <V>           The type of the second key..
	 */
	public static <K, V> void deleteKey(@Nullable final Guild guild, @Nonnull final MapMapConfiguration configuration, @Nonnull final K key, @Nullable final V value){
		if(Objects.nonNull(value)){
			getServerSettings(guild).map(settings -> settings.optJSONObject(configuration.getName())).ifPresent(map -> {
				Optional.ofNullable(map.optJSONObject(key.toString())).ifPresent(map2 -> {
					map2.remove(value.toString());
					if(Objects.equals(map2.length(), 0)){
						map.remove(key.toString());
					}
				});
			});
		}
	}
}
