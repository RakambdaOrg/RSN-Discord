package fr.mrcraftcod.gunterdiscord.commands.config.helpers;

import fr.mrcraftcod.gunterdiscord.commands.config.BaseConfigurationCommand;
import fr.mrcraftcod.gunterdiscord.commands.config.IllegalOperationException;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.settings.ConfigurationOperation;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Color;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class SetConfigurationCommand<T> extends BaseConfigurationCommand{
	public SetConfigurationCommand(@Nullable Command parent){
		super(parent);
	}
	
	@Nonnull
	@Override
	protected Set<ConfigurationOperation> getAllowedOperations(){
		return Set.of(ConfigurationOperation.ADD, ConfigurationOperation.REMOVE, ConfigurationOperation.SHOW);
	}
	
	@Override
	protected void onShow(@Nonnull GuildMessageReceivedEvent event, @Nonnull LinkedList<String> args) throws IllegalOperationException{
		final var values = getConfig(event.getGuild()).stream().flatMap(Set::stream).map(Objects::toString).collect(Collectors.joining(", "));
		final var builder = getConfigEmbed(event, ConfigurationOperation.SHOW.name(), Color.GREEN);
		builder.addField("Values", values, false);
		Actions.reply(event, builder.build());
	}
	
	@Nonnull
	protected abstract Optional<Set<T>> getConfig(@Nonnull Guild guild);
	
	@Override
	protected void onRemove(@Nonnull GuildMessageReceivedEvent event, @Nonnull LinkedList<String> args) throws IllegalOperationException{
		try{
			final var value = extractValue(event, args);
			removeConfig(event.getGuild(), value);
			final var builder = getConfigEmbed(event, ConfigurationOperation.REMOVE.name(), Color.GREEN);
			builder.addField("Value removed", value.toString(), false);
			Actions.reply(event, builder.build());
		}
		catch(IllegalArgumentException e){
			Actions.reply(event, e.getMessage());
		}
	}
	
	@Override
	protected void onSet(@Nonnull GuildMessageReceivedEvent event, @Nonnull LinkedList<String> args) throws IllegalOperationException{
		throw new IllegalOperationException(ConfigurationOperation.SET);
	}
	
	@Override
	protected void onAdd(@Nonnull GuildMessageReceivedEvent event, @Nonnull LinkedList<String> args) throws IllegalOperationException{
		try{
			final var value = extractValue(event, args);
			getConfig(event.getGuild()).ifPresentOrElse(config -> config.add(value), () -> createConfig(event.getGuild(), value));
			final var builder = getConfigEmbed(event, ConfigurationOperation.SET.name(), Color.GREEN);
			builder.addField("Value added", value.toString(), false);
			Actions.reply(event, builder.build());
		}
		catch(IllegalArgumentException e){
			Actions.reply(event, e.getMessage());
		}
	}
	
	protected abstract void createConfig(@Nonnull Guild guild, @Nonnull T value);
	
	@Nonnull
	protected abstract T extractValue(@Nonnull GuildMessageReceivedEvent event, @Nonnull LinkedList<String> args) throws IllegalArgumentException;
	
	protected abstract void removeConfig(@Nonnull Guild guild, @Nonnull T value);
}
