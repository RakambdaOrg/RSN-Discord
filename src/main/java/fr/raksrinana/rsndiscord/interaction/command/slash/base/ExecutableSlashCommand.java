package fr.raksrinana.rsndiscord.interaction.command.slash.base;

import fr.raksrinana.rsndiscord.interaction.command.api.IExecutableCommand;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public abstract class ExecutableSlashCommand implements IExecutableCommand{
	@NotNull
	protected Collection<? extends OptionData> getOptions(){
		return Set.of();
	}
	
	@Override
	@NotNull
	public Map<String, IExecutableCommand> getCommandMap(){
		return Map.of(getId(), this);
	}
	
	@NotNull
	protected final Optional<Integer> getOptionAsInt(@Nullable OptionMapping option){
		return Optional.ofNullable(option)
				.map(OptionMapping::getAsLong)
				.map(Long::intValue);
	}
	
	@NotNull
	protected final <T> Optional<T> getOptionAs(@Nullable OptionMapping option, @NotNull Function<String, T> mapper){
		return Optional.ofNullable(option)
				.map(OptionMapping::getAsString)
				.map(mapper);
	}
}
