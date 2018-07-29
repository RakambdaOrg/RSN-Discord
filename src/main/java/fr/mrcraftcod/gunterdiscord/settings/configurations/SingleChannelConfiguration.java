package fr.mrcraftcod.gunterdiscord.settings.configurations;

import fr.mrcraftcod.gunterdiscord.commands.config.ConfigurationCommand;
import fr.mrcraftcod.gunterdiscord.settings.NoValueDefinedException;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Log;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.awt.*;
import java.io.InvalidClassException;
import java.util.LinkedList;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-05-04
 */
public abstract class SingleChannelConfiguration extends ValueConfiguration{
	@SuppressWarnings("Duplicates")
	@Override
	public ConfigurationCommand.ActionResult handleChange(MessageReceivedEvent event, ConfigurationCommand.ChangeConfigType action, LinkedList<String> args){
		if(action == ConfigurationCommand.ChangeConfigType.SHOW){
			EmbedBuilder builder = new EmbedBuilder();
			builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
			builder.setColor(Color.GREEN);
			builder.setTitle("Valeur de " + getName());
			builder.addField("", getObject(event.getGuild()).toString(), false);
			Actions.reply(event, builder.build());
			return ConfigurationCommand.ActionResult.NONE;
		}
		if(args.size() < 1){
			return ConfigurationCommand.ActionResult.ERROR;
		}
		switch(action){
			case SET:
				setValue(event.getGuild(), event.getMessage().getMentionedChannels().get(0).getIdLong());
				return ConfigurationCommand.ActionResult.OK;
		}
		return ConfigurationCommand.ActionResult.ERROR;
	}
	
	/**
	 * Tells if this config represents the given channel.
	 *
	 * @param channel The channel.
	 *
	 * @return True if the same channels, false otherwise.
	 */
	public boolean isChannel(TextChannel channel){
		if(channel == null){
			return false;
		}
		return isChannel(channel.getGuild(), channel.getIdLong());
	}
	
	/**
	 * Tells if this config represents the given channel.
	 *
	 * @param guild The guild the channel is in.
	 * @param ID    The ID of the channel.
	 *
	 * @return True if the same channels, false otherwise.
	 */
	public boolean isChannel(Guild guild, long ID){
		TextChannel channel = getTextChannel(guild);
		if(channel == null){
			return false;
		}
		return ID == channel.getIdLong();
	}
	
	/**
	 * Get the text channel.
	 *
	 * @param guild The guild.
	 *
	 * @return The text channel or null if not found.
	 */
	public TextChannel getTextChannel(Guild guild){
		try{
			return guild.getJDA().getTextChannelById(getLong(guild));
		}
		catch(InvalidClassException | NoValueDefinedException e){
			Log.debug(guild, "Error getting channel {} from config, value isn't set", getName());
		}
		return null;
	}
}
