package fr.raksrinana.rsndiscord.command2.impl.configuration;

import fr.raksrinana.rsndiscord.settings.impl.GuildConfiguration;
import fr.raksrinana.rsndiscord.settings.types.CategoryConfiguration;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class CategoryConfigurationAccessor extends ValueConfigurationAccessor<CategoryConfiguration>{
	public CategoryConfigurationAccessor(Function<GuildConfiguration, Optional<CategoryConfiguration>> getter, BiConsumer<GuildConfiguration, CategoryConfiguration> setter){
		super(getter, setter);
	}
	
	@Override
	protected CategoryConfiguration fromString(@NotNull String value){
		return new CategoryConfiguration(Long.parseLong(value));
	}
}
