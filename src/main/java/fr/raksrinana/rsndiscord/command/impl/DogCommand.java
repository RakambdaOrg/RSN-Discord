package fr.raksrinana.rsndiscord.command.impl;

import fr.raksrinana.rsndiscord.api.dog.DogApi;
import fr.raksrinana.rsndiscord.command.BasicCommand;
import fr.raksrinana.rsndiscord.command.BotCommand;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.awt.Color.GREEN;

@BotCommand
public class DogCommand extends BasicCommand{
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
				.setImage(DogApi.getDogPictureURL(event.getGuild()))
				.build();
		event.getChannel().sendMessage(embed).submit();
		return SUCCESS;
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
