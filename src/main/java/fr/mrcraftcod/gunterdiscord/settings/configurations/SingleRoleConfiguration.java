package fr.mrcraftcod.gunterdiscord.settings.configurations;

import fr.mrcraftcod.gunterdiscord.commands.config.ConfigurationCommand;
import fr.mrcraftcod.gunterdiscord.settings.NoValueDefinedException;
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
	public ConfigurationCommand.ActionResult handleChange(MessageReceivedEvent event, ConfigurationCommand.ChangeConfigType action, LinkedList<String> args)
	{
		if(action == ConfigurationCommand.ChangeConfigType.SHOW)
		{
			EmbedBuilder builder = new EmbedBuilder();
			builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
			builder.setColor(Color.GREEN);
			builder.setTitle("Valeur de " + getName());
			builder.addField("", getObject(event.getGuild()).toString(), false);
			Actions.reply(event, builder.build());
			return ConfigurationCommand.ActionResult.NONE;
		}
		if(args.size() < 1)
			return ConfigurationCommand.ActionResult.ERROR;
		switch(action)
		{
			case SET:
				setValue(event.getGuild(), event.getMessage().getMentionedRoles().get(0).getIdLong());
				return ConfigurationCommand.ActionResult.OK;
		}
		return ConfigurationCommand.ActionResult.ERROR;
	}
	
	/**
	 * Get the role of this configuration.
	 *
	 * @param guild The guild.
	 *
	 * @return The role or null if not found.
	 */
	public Role getRole(Guild guild)
	{
		try
		{
			return guild.getJDA().getRoleById(getLong(guild));
		}
		catch(InvalidClassException | NoValueDefinedException e)
		{
			Log.error(e, "Error getting role from config");
		}
		return null;
	}
}
