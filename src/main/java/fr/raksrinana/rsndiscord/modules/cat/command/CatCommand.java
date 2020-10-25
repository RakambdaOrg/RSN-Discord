package fr.raksrinana.rsndiscord.modules.cat.command;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.modules.cat.TheCatApi;
import fr.raksrinana.rsndiscord.modules.cat.data.Breed;
import fr.raksrinana.rsndiscord.modules.permission.IPermission;
import fr.raksrinana.rsndiscord.modules.permission.SimplePermission;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.Utilities;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

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
		TheCatApi.getRandomCat().ifPresentOrElse(cat -> {
			final var embed = Utilities.buildEmbed(event.getAuthor(), Color.GREEN, translate(event.getGuild(), "cat.title"), null);
			if(!cat.getBreeds().isEmpty()){
				embed.setDescription(cat.getBreeds().stream().map(Breed::getName).collect(Collectors.joining(" ")));
			}
			embed.setImage(cat.getUrl().toString());
			embed.setFooter(cat.getId());
			Actions.sendEmbed(event.getChannel(), embed.build());
		}, () -> Actions.reply(event, translate(event.getGuild(), "cat.error"), null));
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
