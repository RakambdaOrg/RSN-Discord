package fr.mrcraftcod.gunterdiscord.commands.config.helpers;

import fr.mrcraftcod.gunterdiscord.commands.config.BaseConfigurationCommand;
import fr.mrcraftcod.gunterdiscord.commands.config.IllegalOperationException;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.settings.ConfigurationOperation;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Color;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public abstract class ValueConfigurationCommand<T> extends BaseConfigurationCommand{
	public ValueConfigurationCommand(@Nullable Command parent){
		super(parent);
	}
	
	@Nonnull
	@Override
	protected Set<ConfigurationOperation> getAllowedOperations(){
		return Set.of(ConfigurationOperation.SET, ConfigurationOperation.REMOVE, ConfigurationOperation.SHOW);
	}
	
	@Nonnull
	protected abstract Optional<T> getConfig(Guild guild);
	
	@Override
	protected void onShow(@Nonnull GuildMessageReceivedEvent event, @Nonnull LinkedList<String> args) throws IllegalOperationException{
		final var builder = getConfigEmbed(event, ConfigurationOperation.SHOW.name(), Color.GREEN);
		builder.addField(getValueName(), getConfig(event.getGuild()).map(Objects::toString).orElse("<<EMPTY>>"), false);
		Actions.reply(event, builder.build());
	}
	
	protected abstract String getValueName();
	
	@Override
	protected void onSet(@Nonnull GuildMessageReceivedEvent event, @Nonnull LinkedList<String> args) throws IllegalOperationException{
		try{
			final var value = extractValue(event, args);
			setConfig(event.getGuild(), value);
			final var builder = getConfigEmbed(event, ConfigurationOperation.SET.name(), Color.GREEN);
			builder.addField(getValueName(), value.toString(), false);
			Actions.reply(event, builder.build());
		}
		catch(IllegalArgumentException e){
			Actions.reply(event, e.getMessage());
		}
	}
	
	protected abstract void setConfig(@Nonnull Guild guild, @Nonnull T value);
	
	protected abstract T extractValue(@Nonnull GuildMessageReceivedEvent event, @Nonnull LinkedList<String> args);
	
	@Override
	protected void onRemove(@Nonnull GuildMessageReceivedEvent event, @Nonnull LinkedList<String> args) throws IllegalOperationException{
		removeConfig(event.getGuild());
		final var builder = getConfigEmbed(event, ConfigurationOperation.REMOVE.name(), Color.GREEN);
		builder.addField(getValueName(), "<<EMPTY>>", false);
		Actions.reply(event, builder.build());
	}
	
	protected abstract void removeConfig(@Nonnull Guild guild);
	
	@Override
	protected void onAdd(@Nonnull GuildMessageReceivedEvent event, @Nonnull LinkedList<String> args) throws IllegalOperationException{
		throw new IllegalOperationException(ConfigurationOperation.ADD);
	}
	
	@Override
	public void addHelp(@Nonnull Guild guild, @Nonnull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField(getValueName(), "The value to set", false);
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return super.getDescription() + " [" + getValueName().toLowerCase()+ "]";
	}
}
