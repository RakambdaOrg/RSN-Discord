package fr.rakambda.rsndiscord.spring.interaction.slash.impl.waward;

import org.jetbrains.annotations.NotNull;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

public record Pack(
		@NotNull String name,
		@NotNull Collection<Card> cards
) implements INamed{
	public Pack(@NotNull String name, @NotNull Card... cards){
		this(name, Arrays.asList(cards));
	}
	
	@NotNull
	public Optional<Card> getCard(@NotNull String name){
		return cards.stream()
				.filter(c -> Objects.equals(c.name(), name))
				.findAny();
	}
}
