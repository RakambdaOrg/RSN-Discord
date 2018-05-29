package fr.mrcraftcod.gunterdiscord.settings.configurations;

import fr.mrcraftcod.gunterdiscord.commands.config.ConfigurationCommand;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.awt.*;
import java.util.LinkedList;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/05/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-05-12
 */
public abstract class MultipleRoleConfiguration extends ListConfiguration<Long>
{
	@SuppressWarnings("Duplicates")
	@Override
	public ConfigurationCommand.ActionResult handleChange(MessageReceivedEvent event, ConfigurationCommand.ChangeConfigType action, LinkedList<String> args) throws Exception
	{
		if(action == ConfigurationCommand.ChangeConfigType.SHOW)
		{
			EmbedBuilder builder = new EmbedBuilder();
			builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
			builder.setColor(Color.GREEN);
			builder.setTitle("Valeurs de " + getName());
			getAsList(event.getGuild()).stream().map(r -> event.getJDA().getRoleById(r)).forEach(o -> builder.addField("", "@" + o.getName(), false));
			Actions.reply(event, builder.build());
			return ConfigurationCommand.ActionResult.NONE;
		}
		if(args.size() < 1)
			return ConfigurationCommand.ActionResult.ERROR;
		switch(action)
		{
			case ADD:
				addValue(event.getGuild(), event.getMessage().getMentionedRoles().get(0).getIdLong());
				return ConfigurationCommand.ActionResult.OK;
			case REMOVE:
				removeValue(event.getGuild(), event.getMessage().getMentionedRoles().get(0).getIdLong());
				return ConfigurationCommand.ActionResult.OK;
		}
		return ConfigurationCommand.ActionResult.ERROR;
	}
}
