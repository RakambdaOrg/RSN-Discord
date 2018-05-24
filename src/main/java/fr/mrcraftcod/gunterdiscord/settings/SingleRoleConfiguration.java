package fr.mrcraftcod.gunterdiscord.settings;

import fr.mrcraftcod.gunterdiscord.commands.SetConfigCommand;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Log;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.awt.*;
import java.io.InvalidClassException;
import java.util.LinkedList;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/05/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-05-12
 */
public abstract class SingleRoleConfiguration extends ValueConfiguration
{
	@SuppressWarnings("Duplicates")
	@Override
	public SetConfigCommand.ActionResult handleChange(MessageReceivedEvent event, SetConfigCommand.ChangeConfigType action, LinkedList<String> args)
	{
		if(action == SetConfigCommand.ChangeConfigType.SHOW)
		{
			EmbedBuilder builder = new EmbedBuilder();
			builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
			builder.setColor(Color.GREEN);
			builder.setTitle("Valeur de " + getName());
			builder.addField("", getObject(event.getGuild()).toString(), false);
			Actions.reply(event, builder.build());
			return SetConfigCommand.ActionResult.NONE;
		}
		if(args.size() < 1)
			return SetConfigCommand.ActionResult.ERROR;
		switch(action)
		{
			case SET:
				setValue(event.getGuild(), event.getMessage().getMentionedRoles().get(0).getIdLong());
				return SetConfigCommand.ActionResult.OK;
		}
		return SetConfigCommand.ActionResult.ERROR;
	}
	
	public Role getRole(Guild guild)
	{
		try
		{
			return guild.getJDA().getRoleById(getLong(guild));
		}
		catch(InvalidClassException | NoValueDefinedException e)
		{
			Log.error("Error getting role from config", e);
		}
		return null;
	}
}
