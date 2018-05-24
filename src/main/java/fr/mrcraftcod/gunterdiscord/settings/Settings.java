package fr.mrcraftcod.gunterdiscord.settings;

import fr.mrcraftcod.gunterdiscord.Main;
import fr.mrcraftcod.gunterdiscord.settings.configs.*;
import net.dv8tion.jda.core.entities.Guild;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 09/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-09
 */
public class Settings
{
	private static Path path;
	private static JSONObject settings;
	public static final Class<? extends Configuration>[] SETTINGS = new Class[]{
			BannedRegexConfig.class,
			ModoRolesConfig.class,
			OnlyIdeasConfig.class,
			OnlyImagesConfig.class,
			OnlyQuestionsConfig.class,
			PrefixConfig.class,
			ReportChannelConfig.class,
			PhotoChannelConfig.class,
			QuizChannelConfig.class,
			HangmanChannelConfig.class,
			PhotoConfig.class,
			AutoRolesConfig.class,
			NickDelayConfig.class,
			NickLastChangeConfig.class,
			TrombinoscopeRoleConfig.class
			};
	
	public static Object getObject(Guild guild, String name)
	{
		JSONObject settings = getServerSettings(guild);
		return settings.has(name) ? settings.get(name) : null;
	}
	
	public static void init(Path path) throws IOException
	{
		Settings.path = path;
		if(path.toFile().exists())
			settings = new JSONObject(Files.readAllLines(path).stream().collect(Collectors.joining("")));
		else
			settings = new JSONObject(IOUtils.toString(Main.class.getResourceAsStream("/settings/default.json"), "UTF-8"));
	}
	
	public static Configuration getSettings(String name)
	{
		for(Class<? extends Configuration> klass : SETTINGS)
		{
			try
			{
				Configuration configuration = (Configuration) klass.getConstructors()[0].newInstance();
				if(configuration.getName().equalsIgnoreCase(name))
					return configuration;
			}
			catch(InstantiationException | InvocationTargetException | IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static void save() throws IOException
	{
		Files.write(path, Arrays.asList(settings.toString(4).split("\n")), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
		System.out.println("Config written");
	}
	
	public static void close()
	{
	}
	
	public static JSONObject getServerSettings(Guild guild)
	{
		String id = "" + guild.getIdLong();
		if(settings.has(id))
			return settings.optJSONObject(id);
		settings.put(id, new JSONObject());
		return settings.optJSONObject(id);
	}
	
	public static JSONArray getArray(Guild guild, String name)
	{
		return getServerSettings(guild).optJSONArray(name);
	}
	
	public static void setValue(Guild guild, ValueConfiguration configuration, Object value)
	{
		getServerSettings(guild).put(configuration.getName(), value);
	}
	
	public static <T> void addValue(Guild guild, ListConfiguration configuration, T value)
	{
		getServerSettings(guild).append(configuration.getName(), value);
	}
	
	public static void resetList(Guild guild, ListConfiguration configuration)
	{
		getServerSettings(guild).put(configuration.getName(), new JSONArray());
	}
	
	public static <T> void removeValue(Guild guild, ListConfiguration configuration, T value)
	{
		JSONArray array = getArray(guild, configuration.getName());
		if(array == null)
			return;
		int index = array.toList().indexOf(value);
		if(index != -1)
			array.remove(index);
		getServerSettings(guild).put(configuration.getName(), array);
	}
	
	public static <V, K> void mapValue(Guild guild, MapConfiguration configuration, K key, V value)
	{
		JSONObject map = getServerSettings(guild).optJSONObject(configuration.getName());
		if(map == null)
			map = new JSONObject();
		map.put(key.toString(), value);
		getServerSettings(guild).put(configuration.getName(), map);
	}
	
	public static <K> void deleteKey(Guild guild, MapConfiguration configuration, K key)
	{
		JSONObject map = getServerSettings(guild).optJSONObject(configuration.getName());
		if(map == null)
			return;
		map.remove(key.toString());
	}
	
	public static JSONObject getJSONObject(Guild guild, String name)
	{
		return getServerSettings(guild).optJSONObject(name);
	}
	
	public static void resetMap(Guild guild, MapConfiguration configuration)
	{
		getServerSettings(guild).put(configuration.getName(), new JSONObject());
	}
	
	public static void resetMap(Guild guild, MapListConfiguration configuration)
	{
		getServerSettings(guild).put(configuration.getName(), new JSONObject());
	}
	
	public static <K, V> void mapListValue(Guild guild, MapListConfiguration configuration, K key, V value)
	{
		JSONObject map = getServerSettings(guild).optJSONObject(configuration.getName());
		if(map == null)
			map = new JSONObject();
		map.append(key.toString(), value);
		getServerSettings(guild).put(configuration.getName(), map);
	}
	
	public static <K, V> void deleteKey(Guild guild, MapListConfiguration<K, V> configuration, K key)
	{
		JSONObject map = getServerSettings(guild).optJSONObject(configuration.getName());
		if(map == null)
			return;
		map.remove(key.toString());
	}
	
	public static <K, V> void deleteKey(Guild guild, MapListConfiguration configuration, K key, V value)
	{
		deleteKey(guild, configuration, key, value, Objects::equals);
	}
	
	public static <K, V> void deleteKey(Guild guild, MapListConfiguration configuration, K key, V value, BiFunction<Object, V, Boolean> matcher)
	{
		JSONObject map = getServerSettings(guild).optJSONObject(configuration.getName());
		if(map == null)
			return;
		JSONArray array = map.optJSONArray(key.toString());
		if(array != null)
		{
			int index = -1;
			for(int i = 0; i < array.length(); i++)
			{
				if(matcher.apply(array.get(i), value))
				{
					index = i;
					break;
				}
			}
			if(index != -1)
				array.remove(index);
			map.put(key.toString(), array);
		}
	}
	
	public static <K> void mapMapValue(Guild guild, MapMapConfiguration configuration, K key)
	{
		JSONObject map = getServerSettings(guild).optJSONObject(configuration.getName());
		if(map == null)
			map = new JSONObject();
		if(!map.has(key.toString()))
			map.put(key.toString(), new JSONObject());
		getServerSettings(guild).put(configuration.getName(), map);
	}
	
	public static <V, W, K> void mapMapValue(Guild guild, MapMapConfiguration configuration, K key, V value, W insideValue)
	{
		JSONObject map = getServerSettings(guild).optJSONObject(configuration.getName());
		if(map == null)
			map = new JSONObject();
		if(!map.has(key.toString()))
			map.put(key.toString(), new JSONObject());
		map.optJSONObject(key.toString()).put(value.toString(), insideValue);
		getServerSettings(guild).put(configuration.getName(), map);
	}
	
	public static <K> void deleteKey(Guild guild, MapMapConfiguration configuration, K key)
	{
		JSONObject map = getServerSettings(guild).optJSONObject(configuration.getName());
		if(map == null)
			return;
		map.remove(key.toString());
	}
	
	public static void resetMap(Guild guild, MapMapConfiguration configuration)
	{
		getServerSettings(guild).put(configuration.getName(), new JSONObject());
	}
	
	public static <V, K> void deleteKey(Guild guild, MapMapConfiguration configuration, K key, V value, BiFunction<Object, V, Boolean> matcher)
	{
		JSONObject map = getServerSettings(guild).optJSONObject(configuration.getName());
		if(map == null)
			return;
		JSONObject map2 = map.optJSONObject(key.toString());
		if(map2 != null)
			map2.remove(value.toString());
	}
}
