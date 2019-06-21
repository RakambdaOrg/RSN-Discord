package fr.mrcraftcod.gunterdiscord.settings.configurations;

import fr.mrcraftcod.gunterdiscord.commands.config.ConfigurationCommand;
import fr.mrcraftcod.gunterdiscord.settings.Configuration;
import fr.mrcraftcod.gunterdiscord.settings.NoValueDefinedException;
import fr.mrcraftcod.gunterdiscord.settings.Settings;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Color;
import java.util.*;
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
	ValueConfiguration(@Nullable final Guild guild){
		super(guild);
	}
	
	@Nonnull
	@Override
	public ConfigurationCommand.ActionResult handleChange(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final ConfigurationCommand.ChangeConfigType action, @Nonnull final LinkedList<String> args) throws NoValueDefinedException{
		if(Objects.equals(action, SHOW)){
			final var builder = new EmbedBuilder();
			builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
			builder.setColor(Color.GREEN);
			builder.setTitle("Value of " + getName());
			builder.addField("", getObject().toString(), false);
			Actions.reply(event, builder.build());
			return ConfigurationCommand.ActionResult.NONE;
		}
		if(args.isEmpty()){
			return ConfigurationCommand.ActionResult.ERROR;
		}
		if(Objects.equals(action, SET)){
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
	 */
	@Nonnull
	public Optional<T> getObject() throws IllegalArgumentException{
		if(!Objects.equals(getType(), ConfigType.VALUE)){
			throw new IllegalArgumentException("Not a value config");
		}
		return Settings.getObject(this.guild, getName()).map(object -> getConfigParser().apply(object.toString()));
	}
	
	/**
	 * Set the value.
	 *
	 * @param value the value to set.
	 */
	private void setRawValue(@Nullable final String value){
		Settings.setValue(this.guild, this, value);
	}
	
	/**
	 * Get a parser to parse values from message to the config.
	 *
	 * @return The parser.
	 */
	@Nonnull
	protected abstract BiFunction<GuildMessageReceivedEvent, String, String> getMessageParser();
	
	/**
	 * Get a parser to parse back values from config.
	 *
	 * @return The parser.
	 */
	@Nonnull
	protected abstract Function<String, T> getConfigParser();
	
	@Nonnull
	@Override
	public Collection<ConfigurationCommand.ChangeConfigType> getAllowedActions(){
		return Set.of(SET, SHOW);
	}
	
	@Nonnull
	@Override
	public ConfigType getType(){
		return ConfigType.VALUE;
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
	@Nullable
	public T getObject(@Nullable final T defaultValue) throws IllegalArgumentException{
		return getObject().orElse(defaultValue);
	}
	
	/**
	 * Set the value.
	 *
	 * @param value the value to set.
	 */
	public void setValue(@Nullable final T value){
		setRawValue(getValueParser().apply(value));
	}
	
	/**
	 * Get a parser to convert an object to its config representation.
	 *
	 * @return The parser.
	 */
	@Nonnull
	protected abstract Function<T, String> getValueParser();
}
