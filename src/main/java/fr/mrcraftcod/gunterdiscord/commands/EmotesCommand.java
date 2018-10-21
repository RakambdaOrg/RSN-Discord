package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.settings.configs.EmoteUsageConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-06-16
 */
public class EmotesCommand extends BasicCommand{
	@Override
	public CommandResult execute(@NotNull final MessageReceivedEvent event, @NotNull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		final var config = new EmoteUsageConfig(event.getGuild()).getAsMap();
		final var sorted = config.entrySet().stream().sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
		final var total = sorted.values().stream().mapToLong(l -> l).sum() * 1.0D;
		final var message = new StringBuilder();
		sorted.forEach((key, value) -> {
			final var name = Arrays.stream(key.split(":")).findFirst().orElse("ERROR");
			message.append("Emote :").append(name).append(": utilisation à ").append(100 * value / total).append("%").append("\n");
		});
		Actions.reply(event, message.toString());
		return CommandResult.SUCCESS;
	}
	
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ADMIN;
	}
	
	@Override
	public String getName(){
		return "Utilisation emotes";
	}
	
	@Override
	public List<String> getCommand(){
		return List.of("emote");
	}
	
	@Override
	public String getDescription(){
		return "Obtient la fréquence d'utilisation des emotes";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
