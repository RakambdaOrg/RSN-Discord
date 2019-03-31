package fr.mrcraftcod.gunterdiscord.settings.configs;

import fr.mrcraftcod.gunterdiscord.commands.config.ConfigurationCommand;
import fr.mrcraftcod.gunterdiscord.settings.configurations.MapMapConfiguration;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import java.awt.Color;
import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Function;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-04-15
 */
public class AniListLastAccessConfig extends MapMapConfiguration<Long, String, String>{
	/**
	 * Constructor.
	 *
	 * @param guild The guild for this config.
	 */
	public AniListLastAccessConfig(final Guild guild){
		super(guild);
	}
	
	@Override
	public ConfigurationCommand.ActionResult handleChange(final MessageReceivedEvent event, final ConfigurationCommand.ChangeConfigType action, final LinkedList<String> args){
		if(Objects.equals(action, ConfigurationCommand.ChangeConfigType.SHOW)){
			final var builder = new EmbedBuilder();
			builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
			builder.setColor(Color.GREEN);
			builder.setTitle("Values of " + getName());
			final var map = getAsMap();
			map.keySet().stream().map(k -> new MessageEmbed.Field(k.toString(), map.get(k).toString(), false)).forEach(builder::addField);
			Actions.reply(event, builder.build());
			return ConfigurationCommand.ActionResult.NONE;
		}
		return ConfigurationCommand.ActionResult.ERROR;
	}
	
	@Override
	public String getName(){
		return "aniListLastAccess";
	}
	
	@Override
	protected Function<String, Long> getFirstKeyParser(){
		return Long::parseLong;
	}
	
	@Override
	protected Function<String, String> getSecondKeyParser(){
		return s -> s;
	}
	
	@Override
	protected Function<String, String> getValueParser(){
		return s -> s;
	}
}
