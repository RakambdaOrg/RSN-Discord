package fr.mrcraftcod.gunterdiscord.settings.configs;

import fr.mrcraftcod.gunterdiscord.commands.config.ConfigurationCommand;
import fr.mrcraftcod.gunterdiscord.settings.configurations.MapMapConfiguration;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.awt.*;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-04-15
 */
public class RemoveRoleConfig extends MapMapConfiguration<Long, Long, Long>
{
	@Override
	public ConfigurationCommand.ActionResult handleChange(MessageReceivedEvent event, ConfigurationCommand.ChangeConfigType action, LinkedList<String> args)
	{
		if(action == ConfigurationCommand.ChangeConfigType.SHOW)
		{
			EmbedBuilder builder = new EmbedBuilder();
			builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
			builder.setColor(Color.GREEN);
			builder.setTitle("Valeurs de " + getName());
			Map<Long, Map<Long, Long>> map = getAsMap(event.getGuild());
			map.keySet().stream().map(k -> new MessageEmbed.Field(k.toString(), map.get(k).toString(), false)).forEach(builder::addField);
			Actions.reply(event, builder.build());
			return ConfigurationCommand.ActionResult.NONE;
		}
		return ConfigurationCommand.ActionResult.ERROR;
	}
	
	@Override
	public String getName()
	{
		return "removeRole";
	}
	
	@Override
	protected Function<String, Long> getKeyParser()
	{
		return Long::parseLong;
	}
	
	@Override
	protected Function<String, Long> getKeyValueParser()
	{
		return Long::parseLong;
	}
	
	@Override
	protected Function<String, Long> getValueParser()
	{
		return Long::parseLong;
	}
}
