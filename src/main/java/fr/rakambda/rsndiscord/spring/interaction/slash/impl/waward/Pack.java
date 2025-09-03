package fr.rakambda.rsndiscord.spring.interaction.slash.impl.waward;

import org.jspecify.annotations.NonNull;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

public record Pack(
		@NonNull String name,
		@NonNull Collection<Card> cards
) implements INamed, Comparable<Pack>{
	public Pack(@NonNull String name, @NonNull Card... cards){
		this(name, Arrays.asList(cards));
	}
	
	@NonNull
	public Optional<Card> getCard(@NonNull String name){
		return cards.stream()
				.filter(c -> Objects.equals(c.name(), name))
				.findAny();
	}
	
	@Override
	public int compareTo(@NonNull Pack o){
		return name().compareTo(o.name());
	}
}
