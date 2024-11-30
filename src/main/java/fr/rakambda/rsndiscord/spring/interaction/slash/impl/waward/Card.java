package fr.rakambda.rsndiscord.spring.interaction.slash.impl.waward;

import org.jetbrains.annotations.NotNull;

public record Card(
		@NotNull String name,
		int stars
) implements INamed, Comparable<Card>{
	@Override
	public int compareTo(@NotNull Card o){
		return name().compareTo(o.name());
	}
}
