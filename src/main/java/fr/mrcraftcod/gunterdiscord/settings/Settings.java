package fr.mrcraftcod.gunterdiscord.settings;

import fr.mrcraftcod.gunterdiscord.Main;
import fr.mrcraftcod.gunterdiscord.settings.configs.*;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
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
			OnlyImagesConfig.class,
			OnlyQuestionsConfig.class,
			PrefixConfig.class,
			ReportChannelConfig.class,
			PhotoChannelConfig.class,
			QuizChannelConfig.class,
			HangmanChannelConfig.class,
			PhotoConfig.class,
	};
	
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
	}
	
	public static void close()
	{
	}
	
	public static Object getObject(String name)
	{
		return settings.has(name) ? settings.get(name) : null;
	}
	
	public static JSONArray getArray(String name)
	{
		return settings.optJSONArray(name);
	}
	
	public static void setValue(ValueConfiguration configuration, Object value)
	{
		settings.put(configuration.getName(), value);
	}
	
	public static <T> void addValue(ListConfiguration configuration, T value)
	{
		settings.append(configuration.getName(), value);
	}
	
	public static void resetList(ListConfiguration configuration)
	{
		settings.put(configuration.getName(), new JSONArray());
	}
	
	public static <T> void removeValue(ListConfiguration configuration, T value)
	{
		JSONArray array = getArray(configuration.getName());
		if(array == null)
			return;
		int index = array.toList().indexOf(value);
		if(index != -1)
			array.remove(index);
		settings.put(configuration.getName(), array);
	}
	
	public static <V, K> void mapValue(MapConfiguration configuration, K key, V value)
	{
		JSONObject map = settings.optJSONObject(configuration.getName());
		if(map == null)
			map = new JSONObject();
		map.put(key.toString(), value);
		settings.put(configuration.getName(), map);
	}
	
	public static <K> void deleteKey(MapConfiguration configuration, K key)
	{
		JSONObject map = settings.optJSONObject(configuration.getName());
		if(map == null)
			return;
		map.remove(key.toString());
	}
	
	public static JSONObject getJSONObject(String name)
	{
		return settings.optJSONObject(name);
	}
	
	public static void resetMap(MapConfiguration configuration)
	{
		settings.put(configuration.getName(), new JSONObject());
	}
}
