package fr.raksrinana.rsndiscord.interaction.command.slash.impl.configuration.value;

import fr.raksrinana.rsndiscord.interaction.command.slash.impl.configuration.ValueConfigurationAccessor;
import fr.raksrinana.rsndiscord.settings.impl.GuildConfiguration;
import fr.raksrinana.rsndiscord.settings.types.CategoryConfiguration;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class CategoryConfigurationAccessor extends ValueConfigurationAccessor<CategoryConfiguration>{
	public CategoryConfigurationAccessor(String name, Function<GuildConfiguration, Optional<CategoryConfiguration>> getter, BiConsumer<GuildConfiguration, CategoryConfiguration> setter){
		super(name, getter, setter);
	}
	
	@Override
	protected CategoryConfiguration fromString(@NotNull String value){
		return new CategoryConfiguration(Long.parseLong(value));
	}
}
