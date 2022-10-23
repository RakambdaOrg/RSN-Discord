package fr.rakambda.rsndiscord.spring.interaction.slash.api;

import fr.rakambda.rsndiscord.spring.interaction.slash.permission.IPermission;
import fr.rakambda.rsndiscord.spring.interaction.slash.permission.NoPermission;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Optional;
import java.util.function.Function;

public interface IExecutableSlashCommand{
	@NotNull
	String getId();
	
	@NotNull
	String getPath();
	
	@NotNull
	default IPermission getPermission(){
		return new NoPermission();
	}
	
	@NotNull
	default Optional<Integer> getOptionAsInt(@Nullable OptionMapping option){
		return Optional.ofNullable(option)
				.map(OptionMapping::getAsLong)
				.map(Long::intValue);
	}
	
	@NotNull
	default <T> Optional<T> getOptionAs(@Nullable OptionMapping option, @NotNull Function<String, T> mapper){
		return Optional.ofNullable(option)
				.map(OptionMapping::getAsString)
				.map(mapper);
	}
}
