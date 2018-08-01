package fr.mrcraftcod.gunterdiscord.settings;

import fr.mrcraftcod.gunterdiscord.Main;
import fr.mrcraftcod.gunterdiscord.settings.configs.*;
import fr.mrcraftcod.gunterdiscord.settings.configurations.*;
import net.dv8tion.jda.core.entities.Guild;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.function.BiFunction;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 09/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-09
 */
public class Settings{
	public static final Configuration[] SETTINGS = new Configuration[]{
			// new BannedRegexConfig(null),
			new ModoRolesConfig(null),
			new OnlyIdeasConfig(null),
			new OnlyImagesConfig(null),
			new PrefixConfig(null),
			new ReportChannelConfig(null),
			// new PhotoChannelConfig(null),
			// new QuizChannelConfig(null),
			// new HangmanChannelConfig(null),
			// new PhotoConfig(null),
			new AutoRolesConfig(null),
			new NickDelayConfig(null),
			new NickLastChangeConfig(null),
			// new TrombinoscopeRoleConfig(null),
			new QuestionsChannelConfig(null),
			new QuestionsFinalChannelConfig(null),
			// new HangmanRoleConfig(null),
			// new WerewolvesChannelConfig(null),
			new YoutubeChannelConfig(null),
			new YoutubeRoleConfig(null),
			new MusicPartyChannelConfig(null),
			new WarnRoleConfig(null),
			new DoubleWarnRoleConfig(null),
			new MegaWarnRoleConfig(null),
			new WarnTimeConfig(null),
			new DoubleWarnTimeConfig(null),
			new MegaWarnTimeConfig(null),
			new RemoveRoleConfig(null),
			new VoiceTextChannelsConfig(null)
	};
	private static Path path;
	private static JSONObject settings;
	
	/**
	 * Get the value as an object.
	 *
	 * @param guild The guild.
	 * @param name  The name of the setting.
	 *
	 * @return The object or null if not found.
	 */
	public static Object getObject(Guild guild, String name){
		JSONObject settings = getServerSettings(guild);
		return settings.has(name) ? settings.get(name) : null;
	}
	
	/**
	 * Get the settings for the given guild.
	 *
	 * @param guild The guild.
	 *
	 * @return The settings of the guild.
	 */
	private static JSONObject getServerSettings(Guild guild){
		if(guild == null){
			return new JSONObject();
		}
		JSONObject serverSettings;
		String id = "" + guild.getIdLong();
		if(settings.has(id)){
			serverSettings = settings.optJSONObject(id);
		}
		else{
			settings.put(id, new JSONObject());
			serverSettings = settings.optJSONObject(id);
		}
		return serverSettings == null ? new JSONObject() : serverSettings;
	}
	
	/**
	 * Init the settings.
	 *
	 * @param path The path of the settings to load.
	 *
	 * @throws IOException If something went wrong.
	 */
	public static void init(Path path) throws IOException{
		Settings.path = path;
		if(path.toFile().exists()){
			settings = new JSONObject(String.join("", Files.readAllLines(path)));
		}
		else{
			settings = new JSONObject(IOUtils.toString(Main.class.getResourceAsStream("/settings/default.json"), "UTF-8"));
		}
	}
	
	/**
	 * Get a setting based on its name.
	 *
	 * @param name The name of the config.
	 *
	 * @return The configuration or null if not found.
	 */
	public static Configuration getSettings(String name){
		for(Configuration configuration : SETTINGS){
			if(configuration.getName().equalsIgnoreCase(name)){
				return configuration;
			}
		}
		return null;
	}
	
	/**
	 * Save the settings to the file.
	 *
	 * @throws IOException If something went wrong.
	 */
	public static void save() throws IOException{
		Files.write(path, Arrays.asList(settings.toString(4).split("\n")), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
		System.out.println("Config written");
	}
	
	/**
	 * Closes the settings.
	 */
	public static void close(){
	}
	
	/**
	 * Set the value of a config.
	 *
	 * @param guild         The guild of the config.
	 * @param configuration The configuration.
	 * @param value         The value to set.
	 */
	public static void setValue(Guild guild, ValueConfiguration configuration, Object value){
		getServerSettings(guild).put(configuration.getName(), value);
	}
	
	/**
	 * Add a value.
	 *
	 * @param guild         The guild concerned.
	 * @param configuration The configuration.
	 * @param value         The value to add.
	 * @param <T>           The type of the value.
	 */
	public static <T> void addValue(Guild guild, ListConfiguration configuration, T value){
		getServerSettings(guild).append(configuration.getName(), value);
	}
	
	/**
	 * Clears a list.
	 *
	 * @param guild         The guild concerned.
	 * @param configuration The configuration to clear.
	 */
	public static void resetList(Guild guild, ListConfiguration configuration){
		getServerSettings(guild).put(configuration.getName(), new JSONArray());
	}
	
	/**
	 * Remove a value from a list.
	 *
	 * @param guild         The guild concerned.
	 * @param configuration The configuration.
	 * @param value         The value to remove.
	 * @param <T>           The type of the value.
	 */
	public static <T> void removeValue(Guild guild, ListConfiguration configuration, T value){
		JSONArray array = getArray(guild, configuration.getName());
		if(array == null){
			return;
		}
		int index = array.toList().indexOf(value);
		if(index != -1){
			array.remove(index);
		}
		getServerSettings(guild).put(configuration.getName(), array);
	}
	
	/**
	 * Get an array.
	 *
	 * @param guild The guild concerned.
	 * @param name  The name of the array.
	 *
	 * @return The array or null.
	 */
	public static JSONArray getArray(Guild guild, String name){
		return getServerSettings(guild).optJSONArray(name);
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
	public static <K, V> void mapValue(Guild guild, MapConfiguration configuration, K key, V value){
		JSONObject map = getServerSettings(guild).optJSONObject(configuration.getName());
		if(map == null){
			map = new JSONObject();
		}
		map.put(key.toString(), value);
		getServerSettings(guild).put(configuration.getName(), map);
	}
	
	/**
	 * Delete a key from a map.
	 *
	 * @param guild         The guild concerned.
	 * @param configuration The configuration.
	 * @param key           The key to delete.
	 * @param <K>           The type of the key.
	 */
	public static <K> void deleteKey(Guild guild, MapConfiguration configuration, K key){
		JSONObject map = getServerSettings(guild).optJSONObject(configuration.getName());
		if(map == null){
			return;
		}
		map.remove(key.toString());
	}
	
	/**
	 * Get an object.
	 *
	 * @param guild The guild concerned.
	 * @param name  The name of they key.
	 *
	 * @return The object or null.
	 */
	public static JSONObject getJSONObject(Guild guild, String name){
		return getServerSettings(guild).optJSONObject(name);
	}
	
	/**
	 * Clears a map.
	 *
	 * @param guild         The guild concerned.
	 * @param configuration The configuration.
	 */
	public static void resetMap(Guild guild, MapConfiguration configuration){
		getServerSettings(guild).put(configuration.getName(), new JSONObject());
	}
	
	/**
	 * Clears a map.
	 *
	 * @param guild         The guild concerned.
	 * @param configuration The configuration.
	 */
	public static void resetMap(Guild guild, MapListConfiguration configuration){
		getServerSettings(guild).put(configuration.getName(), new JSONObject());
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
	public static <K, V> void mapListValue(Guild guild, MapListConfiguration configuration, K key, V value){
		JSONObject map = getServerSettings(guild).optJSONObject(configuration.getName());
		if(map == null){
			map = new JSONObject();
		}
		map.append(key.toString(), value);
		getServerSettings(guild).put(configuration.getName(), map);
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
	public static <K, V> void deleteKey(Guild guild, MapListConfiguration<K, V> configuration, K key){
		JSONObject map = getServerSettings(guild).optJSONObject(configuration.getName());
		if(map == null){
			return;
		}
		map.remove(key.toString());
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
	public static <K, V> void deleteKey(Guild guild, MapListConfiguration configuration, K key, V value, BiFunction<Object, V, Boolean> matcher){
		JSONObject map = getServerSettings(guild).optJSONObject(configuration.getName());
		if(map == null){
			return;
		}
		JSONArray array = map.optJSONArray(key.toString());
		if(array != null){
			int index = -1;
			for(int i = 0; i < array.length(); i++){
				if(matcher.apply(array.get(i), value)){
					index = i;
					break;
				}
			}
			if(index != -1){
				array.remove(index);
			}
			if(array.length() == 0){
				map.remove(key.toString());
			}
			else{
				map.put(key.toString(), array);
			}
		}
	}
	
	/**
	 * Add a map inside a map.
	 *
	 * @param guild         The guild concerned.
	 * @param configuration The configuration.
	 * @param key           The key of the map.
	 * @param <K>           The type of the key.
	 */
	public static <K> void mapMapValue(Guild guild, MapMapConfiguration configuration, K key){
		JSONObject map = getServerSettings(guild).optJSONObject(configuration.getName());
		if(map == null){
			map = new JSONObject();
		}
		if(!map.has(key.toString())){
			map.put(key.toString(), new JSONObject());
		}
		getServerSettings(guild).put(configuration.getName(), map);
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
	public static <K, V, W> void mapMapValue(Guild guild, MapMapConfiguration configuration, K key, V value, W insideValue){
		JSONObject map = getServerSettings(guild).optJSONObject(configuration.getName());
		if(map == null){
			map = new JSONObject();
		}
		if(!map.has(key.toString())){
			map.put(key.toString(), new JSONObject());
		}
		map.optJSONObject(key.toString()).put(value.toString(), insideValue);
		getServerSettings(guild).put(configuration.getName(), map);
	}
	
	/**
	 * Delete a map containing a map.
	 *
	 * @param guild         The guild concerned.
	 * @param configuration The configuration.
	 * @param key           The key.
	 * @param <K>           The type of the key.
	 */
	public static <K> void deleteKey(Guild guild, MapMapConfiguration configuration, K key){
		JSONObject map = getServerSettings(guild).optJSONObject(configuration.getName());
		if(map == null){
			return;
		}
		map.remove(key.toString());
	}
	
	/**
	 * Reset a map of map.
	 *
	 * @param guild         The guild concerned.
	 * @param configuration The configuration.
	 */
	public static void resetMap(Guild guild, MapMapConfiguration configuration){
		getServerSettings(guild).put(configuration.getName(), new JSONObject());
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
	public static <K, V> void deleteKey(Guild guild, MapMapConfiguration configuration, K key, V value){
		JSONObject map = getServerSettings(guild).optJSONObject(configuration.getName());
		if(map == null){
			return;
		}
		JSONObject map2 = map.optJSONObject(key.toString());
		if(map2 != null){
			map2.remove(value.toString());
			if(map2.length() == 0){
				map.remove(key.toString());
			}
		}
	}
}
