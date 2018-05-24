package fr.mrcraftcod.gunterdiscord.settings;

import fr.mrcraftcod.gunterdiscord.commands.SetConfigCommand;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.awt.*;
import java.util.LinkedList;
import java.util.Objects;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/05/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-05-12
 */
public abstract class MultipleChannelConfiguration extends ListConfiguration<Long>
{
	@SuppressWarnings("Duplicates")
	@Override
	public SetConfigCommand.ActionResult handleChange(MessageReceivedEvent event, SetConfigCommand.ChangeConfigType action, LinkedList<String> args) throws Exception
	{
		if(action == SetConfigCommand.ChangeConfigType.SHOW)
		{
			EmbedBuilder builder = new EmbedBuilder();
			builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
			builder.setColor(Color.GREEN);
			builder.setTitle("Valeurs de " + getName());
			getAsList(event.getGuild()).stream().map(c -> event.getJDA().getTextChannelById(c)).filter(Objects::nonNull).forEach(o -> builder.addField("", "#" + o.getName(), false));
			Actions.reply(event, builder.build());
			return SetConfigCommand.ActionResult.NONE;
		}
		if(args.size() < 1)
			return SetConfigCommand.ActionResult.ERROR;
		switch(action)
		{
			case ADD:
				addValue(event.getGuild(), event.getMessage().getMentionedChannels().get(0).getIdLong());
				return SetConfigCommand.ActionResult.OK;
			case REMOVE:
				removeValue(event.getGuild(), event.getMessage().getMentionedChannels().get(0).getIdLong());
				return SetConfigCommand.ActionResult.OK;
		}
		return SetConfigCommand.ActionResult.ERROR;
	}
}
