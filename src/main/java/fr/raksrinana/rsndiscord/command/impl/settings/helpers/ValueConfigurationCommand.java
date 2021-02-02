package fr.raksrinana.rsndiscord.command.impl.settings.helpers;

import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.impl.settings.BaseConfigurationCommand;
import fr.raksrinana.rsndiscord.settings.ConfigurationOperation;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import static fr.raksrinana.rsndiscord.settings.ConfigurationOperation.*;
import static java.awt.Color.GREEN;

public abstract class ValueConfigurationCommand<T> extends BaseConfigurationCommand{
	protected ValueConfigurationCommand(Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NotNull Guild guild, @NotNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField(getValueName(), "The value to set", false);
	}
	
	@NotNull
	@Override
	protected Set<ConfigurationOperation> getAllowedOperations(){
		return Set.of(SET, REMOVE, SHOW);
	}
	
	@Override
	protected void onSet(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
		var channel = event.getChannel();
		
		try{
			var value = extractValue(event, args);
			setConfig(event.getGuild(), value);
			var embed = getConfigEmbed(event, SET.name(), GREEN)
					.addField(getValueName(), value.toString(), false)
					.build();
			channel.sendMessage(embed).submit();
		}
		catch(IllegalArgumentException e){
			channel.sendMessage(e.getMessage()).submit();
		}
	}
	
	protected abstract T extractValue(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args);
	
	protected abstract void setConfig(@NotNull Guild guild, @NotNull T value);
	
	@Override
	protected void onRemove(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
		removeConfig(event.getGuild());
		var embed = getConfigEmbed(event, REMOVE.name(), GREEN)
				.addField(getValueName(), "<<EMPTY>>", false)
				.build();
		event.getChannel().sendMessage(embed).submit();
	}
	
	protected abstract void removeConfig(@NotNull Guild guild);
	
	@Override
	protected void onShow(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
		var embed = getConfigEmbed(event, SHOW.name(), GREEN)
				.addField(getValueName(), getConfig(event.getGuild()).map(Objects::toString).orElse("<<EMPTY>>"), false)
				.build();
		event.getChannel().sendMessage(embed).submit();
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return super.getDescription(guild) + " [" + getValueName().toLowerCase() + "]";
	}
	
	@NotNull
	protected abstract Optional<T> getConfig(Guild guild);
	
	@NotNull
	protected abstract String getValueName();
}
