package fr.rakambda.rsndiscord.spring.interaction.context.message.impl;

import fr.rakambda.rsndiscord.spring.amqp.RabbitService;
import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import org.jetbrains.annotations.NotNull;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import static fr.rakambda.rsndiscord.spring.interaction.context.message.impl.R6ContextMenu.Side.ATK;
import static fr.rakambda.rsndiscord.spring.interaction.context.message.impl.R6ContextMenu.Side.DEF;

@RequiredArgsConstructor
public class R6ContextMenu{
	private static final int OPERATOR_COUNT = 3;
	private static final Map<Side, Operator[]> OPERATORS = Map.of(ATK, new Operator[]{
					new Operator(ATK, "Ace"),
					new Operator(ATK, "Amaru"),
					new Operator(ATK, "Ash"),
					new Operator(ATK, "Blackbeard"),
					new Operator(ATK, "Blitz"),
					new Operator(ATK, "Brava"),
					new Operator(ATK, "Buck"),
					new Operator(ATK, "Capitão"),
					new Operator(ATK, "Dokkaebi"),
					new Operator(ATK, "Finka"),
					new Operator(ATK, "Flores"),
					new Operator(ATK, "Fuze"),
					new Operator(ATK, "Glaz"),
					new Operator(ATK, "Gridlock"),
					new Operator(ATK, "Grim"),
					new Operator(ATK, "Hibana"),
					new Operator(ATK, "IQ"),
					new Operator(ATK, "Iana"),
					new Operator(ATK, "Jackal"),
					new Operator(ATK, "Kali"),
					new Operator(ATK, "Lion"),
					new Operator(ATK, "Maverick"),
					new Operator(ATK, "Montagne"),
					new Operator(ATK, "Nøkk"),
					new Operator(ATK, "Nomad"),
					new Operator(ATK, "Osa"),
					new Operator(ATK, "Ram"),
					new Operator(ATK, "Sens"),
					new Operator(ATK, "Sledge"),
					new Operator(ATK, "Tatcher"),
					new Operator(ATK, "Thermite"),
					new Operator(ATK, "Twitch"),
					new Operator(ATK, "Ying"),
					new Operator(ATK, "Zero"),
					new Operator(ATK, "Zofia"),
					},
			DEF, new Operator[]{
					new Operator(DEF, "Alibi"),
					new Operator(DEF, "Aruni"),
					new Operator(DEF, "Azami"),
					new Operator(DEF, "Bandit"),
					new Operator(DEF, "Castle"),
					new Operator(DEF, "Caveira"),
					new Operator(DEF, "Clash"),
					new Operator(DEF, "Doc"),
					new Operator(DEF, "Echo"),
					new Operator(DEF, "Ela"),
					new Operator(DEF, "Fenrir"),
					new Operator(DEF, "Frost"),
					new Operator(DEF, "Goyo"),
					new Operator(DEF, "Jäger"),
					new Operator(DEF, "Kaid"),
					new Operator(DEF, "Kapkan"),
					new Operator(DEF, "Lesion"),
					new Operator(DEF, "Maestro"),
					new Operator(DEF, "Melusi"),
					new Operator(DEF, "Mira"),
					new Operator(DEF, "Mozzie"),
					new Operator(DEF, "Mute"),
					new Operator(DEF, "Oryx"),
					new Operator(DEF, "Pulse"),
					new Operator(DEF, "Rook"),
					new Operator(DEF, "Smoke"),
					new Operator(DEF, "Solis"),
					new Operator(DEF, "Tachanka"),
					new Operator(DEF, "Thorn"),
					new Operator(DEF, "Thunderbird"),
					new Operator(DEF, "Tubarão"),
					new Operator(DEF, "Valkyrie"),
					new Operator(DEF, "Vigil"),
					new Operator(DEF, "Wamai"),
					new Operator(DEF, "Warden"),
					}
	);
	
	private final RabbitService rabbitService;
	
	@NotNull
	protected CompletableFuture<Message> executeGuild(@NotNull MessageContextInteractionEvent event, @NotNull Side side){
		return event.deferReply(false).submit()
				.thenCompose(empty -> JDAWrappers.reply(event, getOperatorMessage(side)).submitAndDelete(15, rabbitService));
	}
	
	@NotNull
	protected Operator getRandomOperator(@NotNull Side side){
		var sideOperators = OPERATORS.get(side);
		return sideOperators[ThreadLocalRandom.current().nextInt(sideOperators.length)];
	}
	
	@NotNull
	protected String getOperatorMessage(@NotNull Side side){
		return IntStream.range(1, OPERATOR_COUNT + 1)
				.mapToObj(i -> "* %d %s".formatted(i, getOperatorMessage(getRandomOperator(side))))
				.collect(Collectors.joining("\n"));
	}
	
	@NotNull
	private String getOperatorMessage(@NotNull Operator operator){
		return "[%s](%s)".formatted(operator.getName(), operator.getIconUrl());
	}
	
	@Data
	protected static class Operator{
		private final Side side;
		private final String name;
		
		public String getIconUrl(){
			return "https://r6operators.marcopixel.eu/icons/png/%s.png".formatted(
					getName().replace("ã", "a")
							.replace("ø", "o")
							.toLowerCase(Locale.ROOT)
			);
		}
	}
	
	protected enum Side{
		ATK, DEF
	}
}
