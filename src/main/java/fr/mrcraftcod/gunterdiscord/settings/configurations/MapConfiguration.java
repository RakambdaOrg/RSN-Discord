package fr.mrcraftcod.gunterdiscord.settings.configurations;

import fr.mrcraftcod.gunterdiscord.commands.SetConfigCommand;
import fr.mrcraftcod.gunterdiscord.settings.Configuration;
import fr.mrcraftcod.gunterdiscord.settings.Settings;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Log;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.json.JSONObject;
import java.awt.*;
import java.io.InvalidClassException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-04-15
 */
public abstract class MapConfiguration<K, V> extends Configuration
{
	private Map<K, V> lastValue = null;
	
	/**
	 * Get the value from the given key.
	 *
	 * @param key The key to get.
	 *
	 * @return The value or null if not found.
	 */
	public V getValue(Guild guild, K key)
	{
		try
		{
			return getAsMap(guild).get(key);
		}
		catch(Exception e)
		{
			Log.error("Can't get value " + getName() + " with key " + key, e);
		}
		return null;
	}
	
	/**
	 * Get the map of this configuration.
	 *
	 * @return The map.
	 *
	 * @throws IllegalArgumentException If this configuration isn't a map.
	 * @throws InvalidClassException    If either they key isn't K or the value isn't V.
	 */
	public Map<K, V> getAsMap(Guild guild) throws IllegalArgumentException
	{
		if(lastValue != null)
			return lastValue;
		Map<K, V> elements = new HashMap<>();
		JSONObject map = getObjectMap(guild);
		if(map == null)
			Settings.resetMap(guild, this);
		else
			for(String key : map.keySet())
			{
				Object value = map.get(key);
				elements.put(getKeyParser().apply(key), getValueParser().apply(value.toString()));
			}
		return elements;
	}
	
	/**
	 * Get the parser to parse back string keys to K.
	 *
	 * @return The parser.
	 */
	protected abstract Function<String, K> getKeyParser();
	
	/**
	 * Get the parser to parse back string values to V.
	 *
	 * @return The parser.
	 */
	protected abstract Function<String, V> getValueParser();
	
	@Override
	public SetConfigCommand.ActionResult handleChange(MessageReceivedEvent event, SetConfigCommand.ChangeConfigType action, LinkedList<String> args) throws Exception
	{
		if(action == SetConfigCommand.ChangeConfigType.SHOW)
		{
			EmbedBuilder builder = new EmbedBuilder();
			builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
			builder.setColor(Color.GREEN);
			builder.setTitle("Valeurs de " + getName());
			Map<K, V> map = getAsMap(event.getGuild());
			map.keySet().stream().map(k -> k + " -> " + map.get(k)).forEach(o -> builder.addField("", o, false));
			Actions.reply(event, builder.build());
			return SetConfigCommand.ActionResult.NONE;
		}
		
		switch(action)
		{
			case ADD:
				if(args.size() < 2)
					return SetConfigCommand.ActionResult.ERROR;
				addValue(event.getGuild(), getKeyParser().apply(args.poll()), getValueParser().apply(args.poll()));
				return SetConfigCommand.ActionResult.OK;
			case REMOVE:
				if(args.size() < 1)
					return SetConfigCommand.ActionResult.ERROR;
				deleteKey(event.getGuild(), getKeyParser().apply(args.poll()));
				return SetConfigCommand.ActionResult.OK;
		}
		return SetConfigCommand.ActionResult.ERROR;
	}
	
	/**
	 * Get the JSON Object.
	 *
	 * @return The JSON object.
	 *
	 * @throws IllegalArgumentException If this configuration isn't a map.
	 */
	private JSONObject getObjectMap(Guild guild) throws IllegalArgumentException
	{
		if(getType() != ConfigType.MAP)
			throw new IllegalArgumentException("Not a map config");
		return Settings.getJSONObject(guild, getName());
	}
	
	@Override
	public ConfigType getType()
	{
		return ConfigType.MAP;
	}
	
	/**
	 * Add a value to the map.
	 *
	 * @param key   The key to add into.
	 * @param value The value to set at the key.
	 */
	public void addValue(Guild guild, K key, V value)
	{
		if(lastValue != null)
			lastValue.put(key, value);
		Settings.mapValue(guild, this, key, value);
	}
	
	/**
	 * Delete the key.
	 *
	 * @param key The key.
	 */
	public void deleteKey(Guild guild, K key)
	{
		if(lastValue != null)
			lastValue.remove(key);
		Settings.deleteKey(guild, this, key);
	}
	
	@Override
	public boolean isActionAllowed(SetConfigCommand.ChangeConfigType action)
	{
		return action == SetConfigCommand.ChangeConfigType.ADD || action == SetConfigCommand.ChangeConfigType.REMOVE || action == SetConfigCommand.ChangeConfigType.SHOW;
	}
}
