package fr.rakambda.rsndiscord.spring.interaction.context.message.impl;

import fr.rakambda.rsndiscord.spring.BotException;
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
public class RandomR6DefContextMenu implements IRegistrableMessageContextMenu, IExecutableMessageContextMenuGuild{
	private static final String[] NAMES = new String[]{
            "Alibi",
            "Aruni",
            "Azami",
            "Bandit",
            "Castle",
            "Caveira",
            "Clash",
            "Doc",
            "Echo",
            "Ela",
            "Fenrir",
            "Frost",
            "Goyo",
            "Jäger",
            "Kaid",
            "Kapkan",
            "Lesion",
            "Maestro",
            "Melusi",
            "Mira",
            "Mozzie",
            "Mute",
            "Oryx",
            "Pulse",
            "Rook",
            "Smoke",
            "Solis",
            "Tachanka",
            "Thorn",
            "Thunderbird",
            "Tubarão",
            "Valkyrie",
            "Vigil",
            "Wamai",
            "Warden"
    };
	
	@Override
	@NotNull
	public String getName(){
		return "Random R6 DEF Operator";
	}
	
	@Override
	@NotNull
	public CommandData getDefinition(@NotNull LocalizationFunction localizationFunction){
		return Commands.message(getName())
				.setLocalizationFunction(localizationFunction)
				.setGuildOnly(true);
	}
	
	@Override
	public @NotNull CompletableFuture<?> executeGuild(@NotNull MessageContextInteractionEvent event, @NotNull Guild guild, @NotNull Member member) throws BotException{
		var deferred = event.deferReply(false).submit();
		
		var name = NAMES[ThreadLocalRandom.current().nextInt(NAMES.length)];
		return deferred.thenCompose(empty -> JDAWrappers.reply(event, name).submit());
	}
}
