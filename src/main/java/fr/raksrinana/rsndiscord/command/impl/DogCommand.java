package fr.raksrinana.rsndiscord.command.impl;

import fr.raksrinana.rsndiscord.api.dog.DogApi;
import fr.raksrinana.rsndiscord.command.BasicCommand;
import fr.raksrinana.rsndiscord.command.BotCommand;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.awt.Color.GREEN;

@BotCommand
public class DogCommand extends BasicCommand{
	@NotNull
	@Override
	public CommandResult execute(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
		super.execute(event, args);
		var embed = new EmbedBuilder().setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl())
				.setColor(GREEN)
				.setTitle(translate(event.getGuild(), "dog.title"))
				.setImage(DogApi.getDogPictureURL(event.getGuild()))
				.build();
		event.getChannel().sendMessage(embed).submit();
		return SUCCESS;
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return new SimplePermission("command.dog", true);
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return translate(guild, "command.dog.name");
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return translate(guild, "command.dog.description");
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("dog");
	}
}
