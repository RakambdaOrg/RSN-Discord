package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.BotCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import fr.mrcraftcod.gunterdiscord.utils.log.Log;
import fr.mrcraftcod.utils.http.requestssenders.get.JSONGetRequestSender;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Color;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-12
 */
@BotCommand
public class DogCommand extends BasicCommand{
	@Nonnull
	@Override
	public CommandResult execute(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		final var embed = Utilities.buildEmbed(event.getAuthor(), Color.GREEN, ":dog: ** | Here's your random dog:**");
		embed.setImage(getDogPictureURL(event.getGuild()));
		Actions.reply(event, embed.build());
		return CommandResult.SUCCESS;
	}
	
	@Nonnull
	private String getDogPictureURL(@Nullable final Guild guild) throws Exception{
		Log.getLogger(guild).debug("Getting random dog picture");
		final var handler = new JSONGetRequestSender(new URL("https://dog.ceo/api/breeds/image/random")).getRequestHandler();
		if(Objects.equals(handler.getStatus(), 200)){
			final var json = handler.getRequestResult().getObject();
			if(json.has("status") && Objects.equals(json.getString("status"), "success")){
				return json.getString("message");
			}
			throw new Exception("Error getting dog API, status isn't successful. Json was: " + json.toString());
		}
		throw new Exception("Error sending API request, HTTP code " + handler.getStatus() + " => " + handler.getRequestResult().toString());
	}
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Dog";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("dog");
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return "Gets a random picture of a dog";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
