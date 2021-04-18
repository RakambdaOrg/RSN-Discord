package fr.raksrinana.rsndiscord.command.impl;

import fr.raksrinana.rsndiscord.api.thecatapi.TheCatApi;
import fr.raksrinana.rsndiscord.api.thecatapi.data.Breed;
import fr.raksrinana.rsndiscord.command.BasicCommand;
import fr.raksrinana.rsndiscord.command.BotCommand;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import static fr.raksrinana.rsndiscord.schedule.ScheduleUtils.deleteMessage;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.awt.Color.GREEN;

@BotCommand
public class CatCommand extends BasicCommand{
	@NotNull
	@Override
	public CommandResult execute(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
		super.execute(event, args);
		
		var guild = event.getGuild();
		var author = event.getAuthor();
		
		TheCatApi.getRandomCat().ifPresentOrElse(cat -> {
			var embed = new EmbedBuilder()
					.setAuthor(author.getName(), null, author.getAvatarUrl())
					.setColor(GREEN)
					.setTitle(translate(guild, "cat.title"));
			if(!cat.getBreeds().isEmpty()){
				embed.setDescription(cat.getBreeds().stream().map(Breed::getName).collect(Collectors.joining(" ")));
			}
			embed.setImage(cat.getUrl().toString())
					.setFooter(cat.getId());
			
			JDAWrappers.message(event, embed.build()).submit();
		}, () -> JDAWrappers.message(event, translate(guild, "cat.error")).submit()
				.thenAccept(deleteMessage(date -> date.plusMinutes(5))));
		return CommandResult.SUCCESS;
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return new SimplePermission("command.cat", true);
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return translate(guild, "command.cat.name");
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return translate(guild, "command.cat.description");
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("cat");
	}
}
