package fr.mrcraftcod.gunterdiscord.settings.configurations;

import fr.mrcraftcod.gunterdiscord.commands.config.ConfigurationCommand;
import fr.mrcraftcod.gunterdiscord.settings.Configuration;
import fr.mrcraftcod.gunterdiscord.settings.Settings;
import net.dv8tion.jda.core.entities.Guild;
import org.json.JSONArray;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-04-15
 */
public abstract class ListConfiguration<T> extends Configuration{
	/**
	 * Add a value to the list.
	 *
	 * @param guild The guild.
	 * @param value The value to add.
	 */
	public void addValue(Guild guild, T value){
		Settings.addValue(guild, this, value);
	}
	
	/**
	 * Remove a value from the list.
	 *
	 * @param guild The guild.
	 * @param value The value to remove.
	 */
	public void removeValue(Guild guild, T value){
		Settings.removeValue(guild, this, value);
	}
	
	/**
	 * Get a list of the values.
	 *
	 * @param guild The guild.
	 *
	 * @return The values list.
	 *
	 * @throws IllegalArgumentException If the configuration isn't a list.
	 */
	public List<T> getAsList(Guild guild) throws IllegalArgumentException{
		List<T> elements = new LinkedList<>();
		JSONArray array = getObjectList(guild);
		if(array == null){
			Settings.resetList(guild, this);
		}
		else{
			for(int i = 0; i < array.length(); i++){
				elements.add(getValueParser().apply(array.get(i).toString()));
			}
		}
		return elements;
	}
	
	/**
	 * Get the JSON array.
	 *
	 * @param guild The guild.
	 *
	 * @return The JSON array.
	 *
	 * @throws IllegalArgumentException If the configuration isn't a list.
	 */
	private JSONArray getObjectList(Guild guild) throws IllegalArgumentException{
		if(getType() != ConfigType.LIST){
			throw new IllegalArgumentException("Not a list config");
		}
		try{
			return Settings.getArray(guild, getName());
		}
		catch(NullPointerException e){
			getLogger(guild).error("NullPointer", e);
		}
		return null;
	}
	
	/**
	 * Tell if an element is inside the list.
	 *
	 * @param guild The guild to fetch the values for.
	 * @param value The value to test.
	 *
	 * @return True if the value is inside, false otherwise.
	 */
	public boolean contains(Guild guild, T value){
		return getAsList(guild).contains(value);
	}
	
	/**
	 * Get the parser to parse back values to T.
	 *
	 * @return The parser.
	 */
	protected abstract Function<String, T> getValueParser();
	
	@Override
	public boolean isActionAllowed(ConfigurationCommand.ChangeConfigType action){
		return action == ConfigurationCommand.ChangeConfigType.ADD || action == ConfigurationCommand.ChangeConfigType.REMOVE || action == ConfigurationCommand.ChangeConfigType.SHOW;
	}
	
	@Override
	public ConfigType getType(){
		return ConfigType.LIST;
	}
}
