package fr.rakambda.rsndiscord.spring.interaction.slash;

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
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SlashCommandService{
	private final Collection<IRegistrableSlashCommand> registrableSlashCommands;
	private final Map<String, IExecutableSlashCommand> executableSlashCommands;
	
	@Autowired
	public SlashCommandService(Collection<IRegistrableSlashCommand> registrableSlashCommands, Collection<IExecutableSlashCommand> executableSlashCommands){
		this.registrableSlashCommands = registrableSlashCommands;
		this.executableSlashCommands = executableSlashCommands.stream()
				.collect(Collectors.toMap(IExecutableSlashCommand::getPath, e -> e));
	}
	
	@NotNull
	private LocalizationFunction getLocalizedFunction(){
		return ResourceBundleLocalizationFunction
				.fromBundles("lang/commands", DiscordLocale.ENGLISH_US, DiscordLocale.FRENCH)
				.build();
	}
	
	@NotNull
	public CompletableFuture<Void> registerGlobalCommands(@NotNull JDA jda){
		log.info("Registering global slash commands");
		
		var localizationFunction = getLocalizedFunction();
		var commands = registrableSlashCommands.stream()
				.map(cmd -> cmd.getDefinition(localizationFunction))
				.collect(Collectors.toSet());
		
		return registerCommands(jda.updateCommands(), commands);
	}
	
	@NotNull
	public CompletableFuture<Void> registerGuildCommands(@NotNull Guild guild){
		log.info("Registering guild slash commands for {}", guild);
		
		var localizationFunction = getLocalizedFunction();
		var commands = registrableSlashCommands.stream()
				.map(cmd -> cmd.getDefinition(localizationFunction))
				.collect(Collectors.toSet());
		
		return registerCommands(guild.updateCommands(), commands);
	}
	
	@NotNull
	private CompletableFuture<Void> registerCommands(@NotNull CommandListUpdateAction action, @NotNull Collection<CommandData> commands){
		if(commands.isEmpty()){
			log.info("No commands to register");
			return CompletableFuture.completedFuture(null);
		}
		
		log.info("Registering {} commands", commands.size());
		return action.addCommands(commands).submit()
				.thenAccept(registered -> log.info("Slash commands registered: {}", registered.stream()
						.map(Command::getName)
						.collect(Collectors.joining(", "))))
				.exceptionally(e -> {
					log.error("Failed to register slash commands", e);
					return null;
				});
	}
	
	@NotNull
	public Optional<IExecutableSlashCommand> getExecutableCommand(@NotNull String fullCommandName){
		return Optional.ofNullable(executableSlashCommands.get(fullCommandName.replace(" ", "/")));
	}
}
