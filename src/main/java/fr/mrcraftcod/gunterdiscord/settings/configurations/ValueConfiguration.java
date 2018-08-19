package fr.mrcraftcod.gunterdiscord.settings.configurations;

import fr.mrcraftcod.gunterdiscord.commands.config.ConfigurationCommand;
import fr.mrcraftcod.gunterdiscord.settings.Configuration;
import fr.mrcraftcod.gunterdiscord.settings.NoValueDefinedException;
import fr.mrcraftcod.gunterdiscord.settings.Settings;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.awt.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import static fr.mrcraftcod.gunterdiscord.commands.config.ConfigurationCommand.ChangeConfigType.SET;
import static fr.mrcraftcod.gunterdiscord.commands.config.ConfigurationCommand.ChangeConfigType.SHOW;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-04-15
 */
public abstract class ValueConfiguration<T> extends Configuration{
	/**
	 * Constructor.
	 *
	 * @param guild The guild for this config.
	 */
	ValueConfiguration(final Guild guild){
		super(guild);
	}
	
	@Override
	public ConfigurationCommand.ActionResult handleChange(final MessageReceivedEvent event, final ConfigurationCommand.ChangeConfigType action, final LinkedList<String> args) throws NoValueDefinedException{
		if(action == SHOW){
			final var builder = new EmbedBuilder();
			builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
			builder.setColor(Color.GREEN);
			builder.setTitle("Valeur de " + getName());
			builder.addField("", getObject().toString(), false);
			Actions.reply(event, builder.build());
			return ConfigurationCommand.ActionResult.NONE;
		}
		if(args.size() < 1){
			return ConfigurationCommand.ActionResult.ERROR;
		}
		switch(action){
			case SET:
				setRawValue(getMessageParser().apply(event, args.poll()));
				return ConfigurationCommand.ActionResult.OK;
		}
		return ConfigurationCommand.ActionResult.OK;
	}
	
	/**
	 * Get the value as an object.
	 *
	 * @return The object or null if not found.
	 *
	 * @throws IllegalArgumentException If this configuration isn't a value.
	 * @throws NoValueDefinedException  If no value is set.
	 */
	public T getObject() throws IllegalArgumentException, NoValueDefinedException{
		if(getType() != ConfigType.VALUE){
			throw new IllegalArgumentException("Not a value config");
		}
		final var obj = Settings.getObject(guild, getName());
		if(Objects.isNull(obj)){
			throw new NoValueDefinedException(this);
		}
		return getConfigParser().apply(obj.toString());
	}
	
	/**
	 * Set the value.
	 *
	 * @param value the value to set.
	 */
	private void setRawValue(final String value){
		Settings.setValue(guild, this, value);
	}
	
	/**
	 * Get a parser to parse values from message to the config.
	 *
	 * @return The parser.
	 */
	protected abstract BiFunction<MessageReceivedEvent, String, String> getMessageParser();
	
	/**
	 * Get a parser to parse back values from config.
	 *
	 * @return The parser.
	 */
	protected abstract Function<String, T> getConfigParser();
	
	@Override
	public Collection<ConfigurationCommand.ChangeConfigType> getAllowedActions(){
		return Set.of(SET, SHOW);
	}
	
	/**
	 * Get the value as an object.
	 *
	 * @param defaultValue The default value to return if none is found.
	 *
	 * @return The object or defaultValue if not found.
	 *
	 * @throws IllegalArgumentException If this configuration isn't a value.
	 */
	public T getObject(final T defaultValue) throws IllegalArgumentException{
		try{
			return getObject();
		}
		catch(final NoValueDefinedException e){
			return defaultValue;
		}
	}
	
	/**
	 * Set the value.
	 *
	 * @param value the value to set.
	 */
	public void setValue(@NotNull final T value){
		setRawValue(getValueParser().apply(value));
	}
	
	/**
	 * Get a parser to convert an object to its config representation.
	 *
	 * @return The parser.
	 */
	protected abstract Function<T, String> getValueParser();
	
	@Override
	public ConfigType getType(){
		return ConfigType.VALUE;
	}
}
