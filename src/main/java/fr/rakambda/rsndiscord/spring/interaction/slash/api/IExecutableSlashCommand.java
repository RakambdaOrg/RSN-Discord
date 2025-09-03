package fr.rakambda.rsndiscord.spring.interaction.slash.api;

import fr.rakambda.rsndiscord.spring.interaction.slash.permission.IPermission;
import fr.rakambda.rsndiscord.spring.interaction.slash.permission.NoPermission;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.util.Optional;
import java.util.function.Function;

public interface IExecutableSlashCommand extends ISlashCommand{
	@NonNull
	String getPath();
	
	@NonNull
	default IPermission getPermission(){
		return new NoPermission();
	}
	
	@NonNull
	default Optional<Integer> getOptionAsInt(@Nullable OptionMapping option){
		return Optional.ofNullable(option)
				.map(OptionMapping::getAsLong)
				.map(Long::intValue);
	}
	
	@NonNull
	default <T> Optional<T> getOptionAs(@Nullable OptionMapping option, @NonNull Function<String, T> mapper){
		return Optional.ofNullable(option)
				.map(OptionMapping::getAsString)
				.map(mapper);
	}
}
