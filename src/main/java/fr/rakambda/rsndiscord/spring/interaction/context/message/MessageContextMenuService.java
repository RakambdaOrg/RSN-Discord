package fr.rakambda.rsndiscord.spring.interaction.context.message;

import fr.rakambda.rsndiscord.spring.interaction.context.message.api.IExecutableMessageContextMenu;
import fr.rakambda.rsndiscord.spring.interaction.context.message.api.IRegistrableMessageContextMenu;
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
public class MessageContextMenuService {
	private final Collection<IRegistrableMessageContextMenu> registrableMessageContextMenus;
	private final Map<String, IExecutableMessageContextMenu> executableMessageContextMenus;
	
	@Autowired
	public MessageContextMenuService(Collection<IRegistrableMessageContextMenu> registrableMessageContextMenus, Collection<IExecutableMessageContextMenu> executableMessageContextMenus){
		this.registrableMessageContextMenus = registrableMessageContextMenus;
		this.executableMessageContextMenus = executableMessageContextMenus.stream()
				.collect(Collectors.toMap(IExecutableMessageContextMenu::getName, e -> e));
	}
	
	@NotNull
	private LocalizationFunction getLocalizedFunction(){
		return ResourceBundleLocalizationFunction
				.fromBundles("lang/context-messages", DiscordLocale.ENGLISH_US, DiscordLocale.FRENCH)
				.build();
	}
	
	@NotNull
	public CompletableFuture<Void> registerGlobalMenus(@NotNull JDA jda){
		log.info("Registering global message context menus");
		
		var localizationFunction = getLocalizedFunction();
		var commands = registrableMessageContextMenus.stream()
				.map(cmd -> cmd.getDefinition(localizationFunction))
				.collect(Collectors.toSet());
		
		return registerMenus(jda.updateCommands(), commands);
	}
	
	@NotNull
	public CompletableFuture<Void> registerGuildMenus(@NotNull Guild guild){
		log.info("Registering guild message context menus for {}", guild);
		
		var localizationFunction = getLocalizedFunction();
		var commands = registrableMessageContextMenus.stream()
				.map(cmd -> cmd.getDefinition(localizationFunction))
				.collect(Collectors.toSet());
		
		return registerMenus(guild.updateCommands(), commands);
	}
	
	@NotNull
	private CompletableFuture<Void> registerMenus(@NotNull CommandListUpdateAction action, @NotNull Collection<CommandData> commands){
		if(commands.isEmpty()){
			log.info("No commands to register");
			return CompletableFuture.completedFuture(null);
		}
		
		log.info("Registering {} commands", commands.size());
		return action.addCommands(commands).submit()
				.thenAccept(registered -> log.info("Message context menus registered: {}", registered.stream()
						.map(Command::getName)
						.collect(Collectors.joining(", "))))
				.exceptionally(e -> {
					log.error("Failed to register message context menus", e);
					return null;
				});
	}
	
	@NotNull
	public Optional<IExecutableMessageContextMenu> getExecutableContextMenu(@NotNull String name){
		return Optional.ofNullable(executableMessageContextMenus.get(name));
	}
}
