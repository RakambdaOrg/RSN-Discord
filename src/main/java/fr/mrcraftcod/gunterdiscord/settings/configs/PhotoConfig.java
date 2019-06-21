package fr.mrcraftcod.gunterdiscord.settings.configs;

import fr.mrcraftcod.gunterdiscord.commands.config.ConfigurationCommand;
import fr.mrcraftcod.gunterdiscord.settings.configurations.MapListConfiguration;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Color;
import java.util.LinkedList;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-04-15
 */
public class PhotoConfig extends MapListConfiguration<Long, String>{
	/**
	 * Constructor.
	 *
	 * @param guild The guild for this config.
	 */
	public PhotoConfig(@Nullable final Guild guild){
		super(guild);
	}
	
	@SuppressWarnings("DuplicatedCode")
	@Nonnull
	@Override
	public ConfigurationCommand.ActionResult handleChange(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final ConfigurationCommand.ChangeConfigType action, @Nonnull final LinkedList<String> args){
		if(Objects.equals(action, ConfigurationCommand.ChangeConfigType.SHOW)){
			final var builder = new EmbedBuilder();
			builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
			builder.setColor(Color.GREEN);
			builder.setTitle("Values of " + getName());
			getAsMap().ifPresent(map -> map.entrySet().stream().map(entry -> new MessageEmbed.Field(entry.getKey().toString(), entry.getValue().toString(), false)).forEach(builder::addField));
			Actions.reply(event, builder.build());
			return ConfigurationCommand.ActionResult.NONE;
		}
		return ConfigurationCommand.ActionResult.ERROR;
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "photo";
	}
	
	@Nonnull
	@Override
	protected Function<String, Long> getKeyParser(){
		return value -> Objects.isNull(value) ? null : Long.parseLong(value);
	}
	
	@Nonnull
	@Override
	protected Function<String, String> getValueParser(){
		return s -> s;
	}
	
	@Nonnull
	@Override
	protected BiFunction<Object, String, Boolean> getMatcher(){
		return (o1, o2) -> Objects.nonNull(o1) && o1.toString().contains(o2);
	}
}
