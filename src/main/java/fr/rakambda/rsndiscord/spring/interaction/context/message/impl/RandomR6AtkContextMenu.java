package fr.rakambda.rsndiscord.spring.interaction.context.message.impl;

import fr.rakambda.rsndiscord.spring.BotException;
import fr.rakambda.rsndiscord.spring.amqp.RabbitService;
import fr.rakambda.rsndiscord.spring.interaction.context.message.api.IExecutableMessageContextMenuGuild;
import fr.rakambda.rsndiscord.spring.interaction.context.message.api.IRegistrableMessageContextMenu;
import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

@Component
@Slf4j
public class RandomR6AtkContextMenu implements IRegistrableMessageContextMenu, IExecutableMessageContextMenuGuild{
	private static final String[] NAMES = new String[]{
			"Ace",
			"Amaru",
			"Ash",
			"Blackbeard",
			"Blitz",
			"Brava",
			"Buck",
			"Capitão",
			"Dokkaebi",
			"Finka",
			"Flores",
			"Fuze",
			"Glaz",
			"Gridlock",
			"Grim",
			"Hibana",
			"IQ",
			"Iana",
			"Jackal",
			"Kali",
			"Lion",
			"Maverick",
			"Montagne",
			"Nokk",
			"Nomad",
			"Osa",
			"Ram",
			"Sens",
			"Sledge",
			"Tatcher",
			"Thermite",
			"Twitch",
			"Ying",
			"Zero",
			"Zofia"
	};
	
	private final RabbitService rabbitService;
	
	public RandomR6AtkContextMenu(RabbitService rabbitService){
		this.rabbitService = rabbitService;
	}
	
	@Override
	@NotNull
	public String getName(){
		return "Random R6 ATK Operator";
	}
	
	@Override
	@NotNull
	public CommandData getDefinition(@NotNull LocalizationFunction localizationFunction){
		return Commands.message(getName())
				.setLocalizationFunction(localizationFunction)
				.setGuildOnly(true);
	}
	
	@Override
	@NotNull
	public CompletableFuture<?> executeGuild(@NotNull MessageContextInteractionEvent event, @NotNull Guild guild, @NotNull Member member) throws BotException{
		var deferred = event.deferReply(false).submit();
		
		var name = NAMES[ThreadLocalRandom.current().nextInt(NAMES.length)];
		return deferred.thenCompose(empty -> JDAWrappers.reply(event, name).submitAndDelete(3, rabbitService));
	}
}
