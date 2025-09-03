package fr.rakambda.rsndiscord.spring.interaction.slash;

import fr.rakambda.rsndiscord.spring.interaction.InteractionsService;
import fr.rakambda.rsndiscord.spring.interaction.slash.api.IExecutableSlashCommand;
import fr.rakambda.rsndiscord.spring.interaction.slash.api.IRegistrableSlashCommand;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import net.dv8tion.jda.api.interactions.commands.localization.ResourceBundleLocalizationFunction;
import net.dv8tion.jda.api.requests.RestAction;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SlashCommandService{
    private final InteractionsService interactionsService;
	private final Collection<IRegistrableSlashCommand> registrableSlashCommands;
	private final Map<String, IExecutableSlashCommand> executableSlashCommands;
	
	@Autowired
	public SlashCommandService(InteractionsService interactionsService, Collection<IRegistrableSlashCommand> registrableSlashCommands, Collection<IExecutableSlashCommand> executableSlashCommands){
        this.interactionsService = interactionsService;
		this.registrableSlashCommands = registrableSlashCommands;
		this.executableSlashCommands = executableSlashCommands.stream()
				.collect(Collectors.toMap(IExecutableSlashCommand::getPath, e -> e));
	}
	
	@NonNull
	private LocalizationFunction getLocalizedFunction(){
		return ResourceBundleLocalizationFunction
				.fromBundles("lang/commands", DiscordLocale.ENGLISH_US, DiscordLocale.FRENCH)
				.build();
	}
	
	@NonNull
	public CompletableFuture<Void> registerGlobalCommands(@NonNull JDA jda){
		log.info("Registering global slash commands");
		
		var localizationFunction = getLocalizedFunction();
		var commands = registrableSlashCommands.stream()
				.filter(IRegistrableSlashCommand::isIncludeAllServers)
				.map(cmd -> cmd.getDefinition(localizationFunction))
				.collect(Collectors.toSet());
		
		return registerCommands(jda::upsertCommand, commands);
	}
	
	@NonNull
	public CompletableFuture<Void> registerGuildCommands(@NonNull Guild guild){
		log.info("Registering guild slash commands for {}", guild);
		
		var localizationFunction = getLocalizedFunction();
		var commands = registrableSlashCommands.stream()
				.filter(cmd -> interactionsService.isCommandActivatedOnGuild(guild, cmd.getRegisterName()))
				.map(cmd -> cmd.getDefinition(localizationFunction))
				.collect(Collectors.toSet());
		
		return registerCommands(guild::upsertCommand, commands);
	}
	
	@NonNull
	private CompletableFuture<Void> registerCommands(@NonNull Function<CommandData, RestAction<Command>> action, @NonNull Collection<CommandData> commands){
		if(commands.isEmpty()){
			log.info("No commands to register");
			return CompletableFuture.completedFuture(null);
		}
		
		log.info("Registering {} commands", commands.size());
		return commands.stream()
				.map(command -> action.apply(command).submit()
						.thenAccept(registered -> log.info("Slash command registered: {}", registered.getName())))
				.reduce((a, b) -> a.thenCombine(b, (v1, v2) -> null))
				.orElseGet(() -> CompletableFuture.completedFuture(null))
				.thenAccept(empty -> {})
				.exceptionally(e -> {
					log.error("Failed to register slash commands", e);
					return null;
				});
	}
	
	@NonNull
	public Optional<IExecutableSlashCommand> getExecutableCommand(@NonNull String fullCommandName){
		return Optional.ofNullable(executableSlashCommands.get(fullCommandName.replace(" ", "/")));
	}
	
	@NonNull
	public Collection<String> getRegistrableCommandNames(){
		return registrableSlashCommands.stream()
				.filter(cmd -> !cmd.isIncludeAllServers())
				.map(IRegistrableSlashCommand::getRegisterName)
				.toList();
	}
	
	public void addCommand(@NonNull JDA jda, long guildId, @NonNull String value){
		registrableSlashCommands.stream()
				.filter(cmd -> Objects.equals(cmd.getRegisterName(), value))
				.forEach(cmd -> jda.getGuildById(guildId).upsertCommand(cmd.getDefinition(getLocalizedFunction())).submit());
	}
	
	public void deleteCommand(@NonNull JDA jda, long guildId, @NonNull String value){
		registrableSlashCommands.stream()
				.filter(cmd -> Objects.equals(cmd.getRegisterName(), value))
				.forEach(cmd -> jda.getGuildById(guildId).deleteCommandById(cmd.getDefinition(getLocalizedFunction()).getName()).submit());
	}
}
