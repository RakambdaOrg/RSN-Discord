package fr.mrcraftcod.gunterdiscord.settings.configs;

import fr.mrcraftcod.gunterdiscord.commands.config.ConfigurationCommand;
import fr.mrcraftcod.gunterdiscord.settings.configurations.ListConfiguration;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.awt.*;
import java.util.LinkedList;
import java.util.function.Function;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-04-15
 */
public class BannedRegexConfig extends ListConfiguration<String>{
	@SuppressWarnings("Duplicates")
	@Override
	public ConfigurationCommand.ActionResult handleChange(MessageReceivedEvent event, ConfigurationCommand.ChangeConfigType action, LinkedList<String> args){
		if(action == ConfigurationCommand.ChangeConfigType.SHOW){
			EmbedBuilder builder = new EmbedBuilder();
			builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
			builder.setColor(Color.GREEN);
			builder.setTitle("Valeurs de " + getName());
			getAsList(event.getGuild()).stream().map(Object::toString).forEach(o -> builder.addField("", o, false));
			Actions.reply(event, builder.build());
			return ConfigurationCommand.ActionResult.NONE;
		}
		if(args.size() < 1){
			return ConfigurationCommand.ActionResult.ERROR;
		}
		switch(action){
			case ADD:
				addValue(event.getGuild(), args.poll());
				return ConfigurationCommand.ActionResult.OK;
			case REMOVE:
				removeValue(event.getGuild(), args.poll());
				return ConfigurationCommand.ActionResult.OK;
		}
		return ConfigurationCommand.ActionResult.ERROR;
	}
	
	@Override
	public String getName(){
		return "bannedRegex";
	}
	
	@Override
	protected Function<String, String> getValueParser(){
		return s -> s;
	}
}
