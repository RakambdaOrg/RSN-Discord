package fr.rakambda.rsndiscord.spring.interaction.slash.impl.waward;

import org.jetbrains.annotations.NotNull;

public record Card(
		@NotNull String name,
		int stars
) implements INamed{
}
