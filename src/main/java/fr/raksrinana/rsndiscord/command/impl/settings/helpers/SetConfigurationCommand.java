package fr.raksrinana.rsndiscord.command.impl.settings.helpers;

import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.impl.settings.BaseConfigurationCommand;
import fr.raksrinana.rsndiscord.settings.ConfigurationOperation;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import static fr.raksrinana.rsndiscord.settings.ConfigurationOperation.*;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.awt.Color.GREEN;

public abstract class SetConfigurationCommand<T> extends BaseConfigurationCommand{
	protected SetConfigurationCommand(Command parent){
		super(parent);
	}
	
	@NotNull
	@Override
	protected Set<ConfigurationOperation> getAllowedOperations(){
		return Set.of(ADD, REMOVE, SHOW);
	}
	
	@Override
	protected void onAdd(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
		var guild = event.getGuild();
		var channel = event.getChannel();
		
		try{
			var value = extractValue(event, args);
			getConfig(guild).ifPresentOrElse(config -> config.add(value), () -> createConfig(guild, value));
			var embed = getConfigEmbed(event, SET.name(), GREEN)
					.addField(translate(guild, "command.config.helpers.value-added"), value.toString(), false)
					.build();
			channel.sendMessage(embed).submit();
		}
		catch(IllegalArgumentException e){
			channel.sendMessage(e.getMessage()).submit();
		}
	}
	
	@Override
	protected void onRemove(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
		var guild = event.getGuild();
		var channel = event.getChannel();
		
		try{
			var value = extractValue(event, args);
			removeConfig(guild, value);
			var embed = getConfigEmbed(event, REMOVE.name(), GREEN)
					.addField(translate(guild, "command.config.helpers.value-removed"), value.toString(), false)
					.build();
			channel.sendMessage(embed).submit();
		}
		catch(IllegalArgumentException e){
			channel.sendMessage(e.getMessage()).submit();
		}
	}
	
	@Override
	protected void onShow(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
		var values = getConfig(event.getGuild()).stream()
				.flatMap(Set::stream)
				.map(Objects::toString)
				.collect(Collectors.joining(", "));
		var embed = getConfigEmbed(event, SHOW.name(), GREEN)
				.addField(translate(event.getGuild(), "command.config.helpers.values"), values, false)
				.build();
		event.getChannel().sendMessage(embed).submit();
	}
	
	protected abstract void removeConfig(@NotNull Guild guild, @NotNull T value);
	
	@NotNull
	protected abstract T extractValue(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args) throws IllegalArgumentException;
	
	@NotNull
	protected abstract Optional<Set<T>> getConfig(@NotNull Guild guild);
	
	protected abstract void createConfig(@NotNull Guild guild, @NotNull T value);
}
