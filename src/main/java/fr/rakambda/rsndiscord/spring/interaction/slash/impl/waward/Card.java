package fr.rakambda.rsndiscord.spring.interaction.slash.impl.waward;

import org.jspecify.annotations.NonNull;

public record Card(
		@NonNull String name,
		int stars
) implements INamed, Comparable<Card>{
	@Override
	public int compareTo(@NonNull Card o){
		return name().compareTo(o.name());
	}
}
