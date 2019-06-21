package fr.mrcraftcod.gunterdiscord.settings.configurations;

import fr.mrcraftcod.gunterdiscord.commands.config.ConfigurationCommand;
import fr.mrcraftcod.gunterdiscord.settings.Configuration;
import fr.mrcraftcod.gunterdiscord.settings.Settings;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.json.JSONObject;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Color;
import java.util.*;
import java.util.function.Function;
import static fr.mrcraftcod.gunterdiscord.commands.config.ConfigurationCommand.ChangeConfigType.*;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-04-15
 */
@SuppressWarnings("WeakerAccess")
public abstract class MapConfiguration<K, V> extends Configuration{
	/**
	 * Constructor.
	 *
	 * @param guild The guild for this config.
	 */
	protected MapConfiguration(@Nullable final Guild guild){
		super(guild);
	}
	
	/**
	 * Get the value from the given key.
	 *
	 * @param key The key to get.
	 *
	 * @return The value or null if not found.
	 */
	@Nonnull
	public Optional<V> getValue(@Nonnull final K key){
		try{
			return getAsMap().map(map -> map.get(key));
		}
		catch(final Exception e){
			getLogger(this.guild).error("Can't get value {} with key {}", getName(), key, e);
		}
		return Optional.empty();
	}
	
	/**
	 * Get the map of this configuration.
	 *
	 * @return The map.
	 *
	 * @throws IllegalArgumentException If this configuration isn't a map.
	 */
	public Optional<Map<K, V>> getAsMap() throws IllegalArgumentException{
		return getObjectMap().map(map -> {
			final Map<K, V> elements = new HashMap<>();
			for(final var key : map.keySet()){
				final var value = map.get(key);
				if(Objects.nonNull(value)){
					elements.put(getKeyParser().apply(key), getValueParser().apply(value.toString()));
				}
			}
			return elements;
		});
	}
	
	/**
	 * Get the JSON Object.
	 *
	 * @return The JSON object.
	 *
	 * @throws IllegalArgumentException If this configuration isn't a map.
	 */
	@Nonnull
	private Optional<JSONObject> getObjectMap() throws IllegalArgumentException{
		if(!Objects.equals(getType(), ConfigType.MAP)){
			throw new IllegalArgumentException("Not a map config");
		}
		return Settings.getJSONObject(this.guild, getName());
	}
	
	/**
	 * Get the parser to parse back string keys to K.
	 *
	 * @return The parser.
	 */
	@Nonnull
	protected abstract Function<String, K> getKeyParser();
	
	/**
	 * Get the parser to parse back string values to V.
	 *
	 * @return The parser.
	 */
	@Nonnull
	protected abstract Function<String, V> getValueParser();
	
	@SuppressWarnings("DuplicatedCode")
	@Nonnull
	@Override
	public ConfigurationCommand.ActionResult handleChange(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final ConfigurationCommand.ChangeConfigType action, @Nonnull final LinkedList<String> args){
		if(Objects.equals(action, SHOW)){
			final var builder = new EmbedBuilder();
			builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
			builder.setColor(Color.GREEN);
			builder.setTitle("Values of " + getName());
			getAsMap().ifPresent(map -> map.entrySet().stream().map(entry -> entry.getKey() + " -> " + entry.getValue()).forEach(o -> builder.addField("", o, false)));
			Actions.reply(event, builder.build());
			return ConfigurationCommand.ActionResult.NONE;
		}
		switch(action){
			case ADD:
				if(args.size() < 2){
					return ConfigurationCommand.ActionResult.ERROR;
				}
				addValue(getKeyParser().apply(args.poll()), getValueParser().apply(args.poll()));
				return ConfigurationCommand.ActionResult.OK;
			case REMOVE:
				if(args.isEmpty()){
					return ConfigurationCommand.ActionResult.ERROR;
				}
				deleteKey(getKeyParser().apply(args.poll()));
				return ConfigurationCommand.ActionResult.OK;
		}
		return ConfigurationCommand.ActionResult.ERROR;
	}
	
	@Nonnull
	@Override
	public Collection<ConfigurationCommand.ChangeConfigType> getAllowedActions(){
		return Set.of(ADD, SHOW, REMOVE);
	}
	
	@Nonnull
	@Override
	public ConfigType getType(){
		return ConfigType.MAP;
	}
	
	/**
	 * Add a value to the map.
	 *
	 * @param key   The key to add into.
	 * @param value The value to set at the key.
	 */
	public void addValue(@Nonnull final K key, @Nullable final V value){
		Settings.mapValue(this.guild, this, key, value);
	}
	
	/**
	 * Delete the key.
	 *
	 * @param key The key.
	 */
	public void deleteKey(@Nonnull final K key){
		Settings.deleteKey(this.guild, this, key);
	}
}
