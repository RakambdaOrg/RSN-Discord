package fr.mrcraftcod.gunterdiscord.settings.configurations;

import fr.mrcraftcod.gunterdiscord.commands.config.ConfigurationCommand;
import fr.mrcraftcod.gunterdiscord.settings.Configuration;
import fr.mrcraftcod.gunterdiscord.settings.Settings;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.json.JSONObject;
import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Function;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-04-15
 */
public abstract class MapConfiguration<K, V> extends Configuration{
	/**
	 * Get the value from the given key.
	 *
	 * @param guild The guild.
	 * @param key   The key to get.
	 *
	 * @return The value or null if not found.
	 */
	public V getValue(Guild guild, K key){
		try{
			return getAsMap(guild).get(key);
		}
		catch(Exception e){
			getLogger(guild).error("Can't get value {} with key {}", getName(), key, e);
		}
		return null;
	}
	
	/**
	 * Get the map of this configuration.
	 *
	 * @param guild The guild.
	 *
	 * @return The map.
	 *
	 * @throws IllegalArgumentException If this configuration isn't a map.
	 */
	public Map<K, V> getAsMap(Guild guild) throws IllegalArgumentException{
		Map<K, V> elements = new HashMap<>();
		JSONObject map = getObjectMap(guild);
		if(map == null){
			Settings.resetMap(guild, this);
		}
		else{
			for(String key : map.keySet()){
				Object value = map.get(key);
				elements.put(getKeyParser().apply(key), getValueParser().apply(value.toString()));
			}
		}
		return elements;
	}
	
	/**
	 * Get the JSON Object.
	 *
	 * @param guild The guild.
	 *
	 * @return The JSON object.
	 *
	 * @throws IllegalArgumentException If this configuration isn't a map.
	 */
	private JSONObject getObjectMap(Guild guild) throws IllegalArgumentException{
		if(getType() != ConfigType.MAP){
			throw new IllegalArgumentException("Not a map config");
		}
		return Settings.getJSONObject(guild, getName());
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
	public boolean isActionAllowed(ConfigurationCommand.ChangeConfigType action){
		return action == ConfigurationCommand.ChangeConfigType.ADD || action == ConfigurationCommand.ChangeConfigType.REMOVE || action == ConfigurationCommand.ChangeConfigType.SHOW;
	}
	
	@Override
	public ConfigurationCommand.ActionResult handleChange(MessageReceivedEvent event, ConfigurationCommand.ChangeConfigType action, LinkedList<String> args){
		if(action == ConfigurationCommand.ChangeConfigType.SHOW){
			EmbedBuilder builder = new EmbedBuilder();
			builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
			builder.setColor(Color.GREEN);
			builder.setTitle("Valeurs de " + getName());
			Map<K, V> map = getAsMap(event.getGuild());
			map.keySet().stream().map(k -> k + " -> " + map.get(k)).forEach(o -> builder.addField("", o, false));
			Actions.reply(event, builder.build());
			return ConfigurationCommand.ActionResult.NONE;
		}
		
		switch(action){
			case ADD:
				if(args.size() < 2){
					return ConfigurationCommand.ActionResult.ERROR;
				}
				addValue(event.getGuild(), getKeyParser().apply(args.poll()), getValueParser().apply(args.poll()));
				return ConfigurationCommand.ActionResult.OK;
			case REMOVE:
				if(args.size() < 1){
					return ConfigurationCommand.ActionResult.ERROR;
				}
				deleteKey(event.getGuild(), getKeyParser().apply(args.poll()));
				return ConfigurationCommand.ActionResult.OK;
		}
		return ConfigurationCommand.ActionResult.ERROR;
	}
	
	@Override
	public ConfigType getType(){
		return ConfigType.MAP;
	}
	
	/**
	 * Add a value to the map.
	 *
	 * @param guild The guild.
	 * @param key   The key to add into.
	 * @param value The value to set at the key.
	 */
	public void addValue(Guild guild, K key, V value){
		Settings.mapValue(guild, this, key, value);
	}
	
	/**
	 * Delete the key.
	 *
	 * @param guild The guild.
	 * @param key   The key.
	 */
	public void deleteKey(Guild guild, K key){
		Settings.deleteKey(guild, this, key);
	}
}
