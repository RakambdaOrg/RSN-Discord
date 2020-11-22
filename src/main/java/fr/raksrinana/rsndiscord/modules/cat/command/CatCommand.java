package fr.raksrinana.rsndiscord.modules.cat.command;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.modules.cat.TheCatApi;
import fr.raksrinana.rsndiscord.modules.cat.data.Breed;
import fr.raksrinana.rsndiscord.modules.permission.IPermission;
import fr.raksrinana.rsndiscord.modules.permission.SimplePermission;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import static fr.raksrinana.rsndiscord.modules.schedule.ScheduleUtils.deleteMessage;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.awt.Color.GREEN;

@BotCommand
public class CatCommand extends BasicCommand{
	@Override
	public @NonNull IPermission getPermission(){
		return new SimplePermission("command.cat", true);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		
		var guild = event.getGuild();
		var author = event.getAuthor();
		var channel = event.getChannel();
		
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
			
			channel.sendMessage(embed.build()).submit();
		}, () -> channel.sendMessage(translate(guild, "cat.error")).submit()
				.thenAccept(deleteMessage(date -> date.plusMinutes(5))));
		return CommandResult.SUCCESS;
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.cat.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("cat");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.cat.description");
	}
}
