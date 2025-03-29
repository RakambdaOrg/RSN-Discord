package fr.rakambda.rsndiscord.spring.interaction.context.message.impl;

import fr.rakambda.rsndiscord.spring.BotException;
import fr.rakambda.rsndiscord.spring.amqp.RabbitService;
import fr.rakambda.rsndiscord.spring.interaction.context.message.api.IExecutableMessageContextMenuGuild;
import fr.rakambda.rsndiscord.spring.interaction.context.message.api.IRegistrableMessageContextMenu;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;
import static fr.rakambda.rsndiscord.spring.interaction.context.message.impl.R6ContextMenu.Side.ATK;

@Component
@Slf4j
public class RandomR6AtkContextMenu extends R6ContextMenu implements IRegistrableMessageContextMenu, IExecutableMessageContextMenuGuild{
	@Autowired
	public RandomR6AtkContextMenu(RabbitService rabbitService){
		super(rabbitService);
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
				.setContexts(InteractionContextType.GUILD);
	}
	
	@Override
	@NotNull
	public CompletableFuture<?> executeGuild(@NotNull MessageContextInteractionEvent event, @NotNull Guild guild, @NotNull Member member) throws BotException{
		return executeGuild(event, ATK);
	}
}
