package fr.mrcraftcod.gunterdiscord.commands.newconfig.helpers;

import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.newconfig.BaseConfigurationCommand;
import fr.mrcraftcod.gunterdiscord.commands.newconfig.IllegalOperationException;
import fr.mrcraftcod.gunterdiscord.newSettings.ConfigurationOperation;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class ListConfigurationCommand<T> extends BaseConfigurationCommand{
	public ListConfigurationCommand(@Nullable Command parent){
		super(parent);
	}
	
	@Nonnull
	@Override
	protected List<ConfigurationOperation> getAllowedOperations(){
		return List.of(ConfigurationOperation.ADD, ConfigurationOperation.REMOVE, ConfigurationOperation.SHOW);
	}
	
	@Nonnull
	protected abstract Optional<List<T>> getConfig(@Nonnull Guild guild);
	
	@Override
	protected void onShow(@Nonnull GuildMessageReceivedEvent event, @Nonnull LinkedList<String> args) throws IllegalOperationException{
		final var values = getConfig(event.getGuild()).stream().flatMap(List::stream).map(Objects::toString).collect(Collectors.joining(", "));
		final var builder = getConfigEmbed(event, ConfigurationOperation.SHOW.name(), Color.GREEN);
		builder.addField("Values", values, false);
		Actions.reply(event, builder.build());
	}
	
	@Override
	protected void onSet(@Nonnull GuildMessageReceivedEvent event, @Nonnull LinkedList<String> args) throws IllegalOperationException{
		throw new IllegalOperationException(ConfigurationOperation.SET);
	}
	
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
	
	protected abstract void removeConfig(@Nonnull Guild guild, @Nonnull T value);
	
	protected abstract void createConfig(@Nonnull Guild guild, @Nonnull T value);
	
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
	
	@Nonnull
	protected abstract T extractValue(@Nonnull GuildMessageReceivedEvent event, @Nonnull LinkedList<String> args) throws IllegalArgumentException;
}
