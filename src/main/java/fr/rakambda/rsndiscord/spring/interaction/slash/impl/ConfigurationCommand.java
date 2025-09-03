package fr.rakambda.rsndiscord.spring.interaction.slash.impl;

import fr.rakambda.rsndiscord.spring.configuration.ConfigurationOperation;
import fr.rakambda.rsndiscord.spring.configuration.IConfigurationAccessor;
import fr.rakambda.rsndiscord.spring.interaction.exception.InvalidOperationTypeException;
import fr.rakambda.rsndiscord.spring.interaction.exception.OperationNotSupportedException;
import fr.rakambda.rsndiscord.spring.interaction.exception.UnknownAccessorException;
import fr.rakambda.rsndiscord.spring.interaction.slash.api.IExecutableSlashCommandGuild;
import fr.rakambda.rsndiscord.spring.interaction.slash.api.IRegistrableSlashCommand;
import fr.rakambda.rsndiscord.spring.jda.ActionWrapper;
import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

@Component
@Slf4j
public class ConfigurationCommand implements IRegistrableSlashCommand, IExecutableSlashCommandGuild{
	private static final String OPERATION_OPTION_ID = "operation";
	private static final String NAME_OPTION_ID = "name";
	private static final String VALUE_OPTION_ID = "value";
	
	private static final String SET_OPERATION_TYPE = "set";
	private static final String RESET_OPERATION_TYPE = "reset";
	private static final String ADD_OPERATION_TYPE = "add";
	private static final String REMOVE_OPERATION_TYPE = "remove";
	private static final String SHOW_OPERATION_TYPE = "show";
	
	private final Map<String, IConfigurationAccessor> accessors;
	private final Supplier<UnknownAccessorException> unknownAccessorExceptionGenerator;
	
	@Autowired
	public ConfigurationCommand(Collection<IConfigurationAccessor> accessors){
		this.accessors = accessors.stream().collect(Collectors.toMap(IConfigurationAccessor::getName, a -> a));
		
		var accessorNames = this.accessors.keySet().stream().sorted().collect(Collectors.joining("\n"));
		unknownAccessorExceptionGenerator = () -> new UnknownAccessorException(accessorNames);
	}
	
	@Override
	@NonNull
	public String getId(){
		return "configuration";
	}
	
	@Override
	@NonNull
	public String getPath(){
		return getId();
	}
	
	@Override
	@NonNull
	public CommandData getDefinition(@NonNull LocalizationFunction localizationFunction){
		return Commands.slash("configuration", "Change the bot's configuration")
				.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
				.setLocalizationFunction(localizationFunction)
				.setContexts(InteractionContextType.GUILD)
				.addOptions(
						new OptionData(STRING, NAME_OPTION_ID, "Name of the configuration to change")
								.setRequired(true)
								.setAutoComplete(true),
						new OptionData(STRING, OPERATION_OPTION_ID, "The operation to perform")
								.addChoice("set", SET_OPERATION_TYPE)
								.addChoice("reset", RESET_OPERATION_TYPE)
								.addChoice("add", ADD_OPERATION_TYPE)
								.addChoice("remove", REMOVE_OPERATION_TYPE)
								.addChoice("show", SHOW_OPERATION_TYPE)
								.setRequired(true),
						new OptionData(STRING, VALUE_OPTION_ID, "Value to set")
								.setRequired(false)
								.setAutoComplete(true));
	}
	
	@Override
	@NonNull
	public CompletableFuture<?> executeGuild(@NonNull SlashCommandInteraction event, @NonNull Guild guild, @NonNull Member member) throws UnknownAccessorException, InvalidOperationTypeException, OperationNotSupportedException{
		var deferred = event.deferReply(true).submit();
		
		var accessor = Optional.ofNullable(accessors.get(event.getOption(NAME_OPTION_ID).getAsString())).orElseThrow(unknownAccessorExceptionGenerator);
		var operation = event.getOption(OPERATION_OPTION_ID).getAsString();
		
		var guildId = event.getGuild().getIdLong();
		var operationType = ConfigurationOperation.fromValue(operation).orElseThrow(InvalidOperationTypeException::new);
		
		return deferred
				.thenCompose(switch(operationType){
					case SET -> empty -> handleSetOperation(event, guildId, accessor);
					case RESET -> empty -> handleResetOperation(event, guildId, accessor);
					case ADD -> empty -> handleAddOperation(event, guildId, accessor);
					case REMOVE -> empty -> handleRemoveOperation(event, guildId, accessor);
					case SHOW -> empty -> handleShowOperation(event, guildId, accessor);
				})
				.exceptionally(e -> {
					if(e instanceof DataAccessException){
						throw new RuntimeException("Failed to perform database update", e);
					}
					if(e instanceof RuntimeException runtimeException){
						throw runtimeException;
					}
					throw new RuntimeException(e);
				});
	}
	
	@NonNull
	private CompletableFuture<Message> handleSetOperation(@NonNull SlashCommandInteraction event, long guildId, @NonNull IConfigurationAccessor accessor){
		try{
			if(accessor.set(event.getJDA(), guildId, event.getOption(VALUE_OPTION_ID).getAsString())){
				return JDAWrappers.edit(event, "Value modified").submit();
			}
			return JDAWrappers.edit(event, "Failed to set value").submit();
		}
		catch(OperationNotSupportedException e){
			return CompletableFuture.failedFuture(e);
		}
	}
	
	@NonNull
	private CompletableFuture<Message> handleResetOperation(@NonNull SlashCommandInteraction event, long guildId, @NonNull IConfigurationAccessor accessor){
		try{
			if(accessor.reset(event.getJDA(), guildId)){
				return JDAWrappers.edit(event, "Value reset").submit();
			}
			return JDAWrappers.edit(event, "Failed to reset value").submit();
		}
		catch(OperationNotSupportedException e){
			return CompletableFuture.failedFuture(e);
		}
	}
	
	@NonNull
	private CompletableFuture<Message> handleAddOperation(@NonNull SlashCommandInteraction event, long guildId, @NonNull IConfigurationAccessor accessor){
		try{
			if(accessor.add(event.getJDA(), guildId, event.getOption(VALUE_OPTION_ID).getAsString())){
				return JDAWrappers.edit(event, "Value added").submit();
			}
			return JDAWrappers.edit(event, "Failed to add value").submit();
		}
		catch(OperationNotSupportedException e){
			return CompletableFuture.failedFuture(e);
		}
	}
	
	@NonNull
	private CompletableFuture<Message> handleRemoveOperation(@NonNull SlashCommandInteraction event, long guildId, @NonNull IConfigurationAccessor accessor){
		try{
			if(accessor.remove(event.getJDA(), guildId, event.getOption(VALUE_OPTION_ID).getAsString())){
				return JDAWrappers.edit(event, "Value removed").submit();
			}
			return JDAWrappers.edit(event, "Failed to remove value").submit();
		}
		catch(OperationNotSupportedException e){
			return CompletableFuture.failedFuture(e);
		}
	}
	
	@NonNull
	private CompletableFuture<Message> handleShowOperation(@NonNull SlashCommandInteraction event, long guildId, @NonNull IConfigurationAccessor accessor){
		try{
			return accessor.show(guildId)
					.map(embed -> JDAWrappers.edit(event, embed).submit())
					.orElseGet(() -> JDAWrappers.edit(event, "Failed to get value").submit());
		}
		catch(OperationNotSupportedException e){
			return CompletableFuture.failedFuture(e);
		}
	}
	
	@Override
	@NonNull
	public CompletableFuture<?> autoCompleteGuild(@NonNull CommandAutoCompleteInteractionEvent event, @NonNull Guild guild, @NonNull Member member){
		return switch(event.getFocusedOption().getName()){
			case NAME_OPTION_ID -> autoCompleteName(event);
			case VALUE_OPTION_ID -> autoCompleteValue(event);
			default -> CompletableFuture.completedFuture(null);
		};
	}
	
	@NonNull
	private CompletableFuture<Void> autoCompleteName(@NonNull CommandAutoCompleteInteractionEvent event){
		var choices = getConfigNamesStartingWith(event.getFocusedOption().getValue())
				.limit(OptionData.MAX_CHOICES)
				.sorted()
				.map(name -> new Command.Choice(name, name))
				.toList();
		return JDAWrappers.reply(event, choices).submit();
	}
	
	@NonNull
	private CompletableFuture<Void> autoCompleteValue(@NonNull CommandAutoCompleteInteractionEvent event){
		return Optional.ofNullable(event.getOption(NAME_OPTION_ID))
				.map(OptionMapping::getAsString)
				.map(accessors::get)
				.map(accessor -> accessor.autoComplete(event))
				.filter(c -> !c.isEmpty())
				.map(choices -> JDAWrappers.reply(event, choices))
				.map(ActionWrapper::submit)
				.orElseGet(() -> CompletableFuture.completedFuture(null));
	}
	
	@NonNull
	private Stream<String> getConfigNamesStartingWith(@NonNull String value){
		if(value.isBlank()){
			return accessors.keySet().stream();
		}
		return accessors.keySet().stream().filter(name -> name.startsWith(value));
	}
	
	@Override
	public boolean isIncludeAllServers(){
		return true;
	}
}
