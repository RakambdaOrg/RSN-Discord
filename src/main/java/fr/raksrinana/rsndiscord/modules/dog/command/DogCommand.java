package fr.raksrinana.rsndiscord.modules.dog.command;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.modules.dog.data.DogResponse;
import fr.raksrinana.rsndiscord.modules.permission.IPermission;
import fr.raksrinana.rsndiscord.modules.permission.SimplePermission;
import fr.raksrinana.rsndiscord.utils.InvalidResponseException;
import kong.unirest.Unirest;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.commands.generic.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.awt.Color.GREEN;

@BotCommand
public class DogCommand extends BasicCommand{
	private static final int HTTP_OK = 200;
	
	@Override
	public @NonNull IPermission getPermission(){
		return new SimplePermission("command.dog", true);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		var embed = new EmbedBuilder().setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl())
				.setColor(GREEN)
				.setTitle(translate(event.getGuild(), "dog.title"))
				.setImage(DogCommand.getDogPictureURL(event.getGuild()))
				.build();
		event.getChannel().sendMessage(embed).submit();
		return SUCCESS;
	}
	
	@NonNull
	private static String getDogPictureURL(final Guild guild){
		Log.getLogger(guild).debug("Getting random dog picture");
		
		var response = Unirest.get("https://dog.ceo/api/breeds/image/random").asObject(DogResponse.class);
		if(response.isSuccess()){
			var dog = response.getBody();
			if(dog.isSuccess()){
				return dog.getMessage();
			}
			throw new InvalidResponseException("Error getting dog API, status isn't successful. " + dog);
		}
		throw new InvalidResponseException("Error sending API request, HTTP code " + response.getStatus());
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.dog.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("dog");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.dog.description");
	}
}
