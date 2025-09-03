package fr.rakambda.rsndiscord.spring.interaction.context.message;

import fr.rakambda.rsndiscord.spring.interaction.InteractionsService;
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
public class MessageContextMenuService{
	private final InteractionsService interactionsService;
	private final Collection<IRegistrableMessageContextMenu> registrableMessageContextMenus;
	private final Map<String, IExecutableMessageContextMenu> executableMessageContextMenus;
	
	@Autowired
	public MessageContextMenuService(InteractionsService interactionsService, Collection<IRegistrableMessageContextMenu> registrableMessageContextMenus, Collection<IExecutableMessageContextMenu> executableMessageContextMenus){
		this.interactionsService = interactionsService;
		this.registrableMessageContextMenus = registrableMessageContextMenus;
		this.executableMessageContextMenus = executableMessageContextMenus.stream()
				.collect(Collectors.toMap(IExecutableMessageContextMenu::getName, e -> e));
	}
	
	@NonNull
	private LocalizationFunction getLocalizedFunction(){
		return ResourceBundleLocalizationFunction
				.fromBundles("lang/context-messages", DiscordLocale.ENGLISH_US, DiscordLocale.FRENCH)
				.build();
	}
	
	@NonNull
	public CompletableFuture<Void> registerGlobalMenus(@NonNull JDA jda){
		log.info("Registering global message context menus");
		
		var localizationFunction = getLocalizedFunction();
		var commands = registrableMessageContextMenus.stream()
				.filter(IRegistrableMessageContextMenu::isIncludeAllServers)
				.map(cmd -> cmd.getDefinition(localizationFunction))
				.collect(Collectors.toSet());
		
		return registerMenus(jda::upsertCommand, commands);
	}
	
	@NonNull
	public CompletableFuture<Void> registerGuildMenus(@NonNull Guild guild){
		log.info("Registering guild message context menus for {}", guild);
		
		var localizationFunction = getLocalizedFunction();
		var commands = registrableMessageContextMenus.stream()
				.filter(cmd -> interactionsService.isCommandActivatedOnGuild(guild, cmd.getRegisterName()))
				.map(cmd -> cmd.getDefinition(localizationFunction))
				.collect(Collectors.toSet());
		
		return registerMenus(guild::upsertCommand, commands);
	}
	
	@NonNull
	private CompletableFuture<Void> registerMenus(@NonNull Function<CommandData, RestAction<Command>> action, @NonNull Collection<CommandData> commands){
		if(commands.isEmpty()){
			log.info("No message context menus to register");
			return CompletableFuture.completedFuture(null);
		}
		
		log.info("Registering {} message context menus", commands.size());
		return commands.stream()
				.map(command -> action.apply(command).submit()
						.thenAccept(registered -> log.info("Message context menus registered: {}", registered.getName())))
				.reduce((a, b) -> a.thenCombine(b, (v1, v2) -> null))
				.orElseGet(() -> CompletableFuture.completedFuture(null))
				.thenAccept(empty -> {})
				.exceptionally(e -> {
					log.error("Failed to register message context menus", e);
					return null;
				});
	}
	
	@NonNull
	public Optional<IExecutableMessageContextMenu> getExecutableContextMenu(@NonNull String name){
		return Optional.ofNullable(executableMessageContextMenus.get(name));
	}
	
	@NonNull
	public Collection<String> getRegistrableContextMenu(){
		return registrableMessageContextMenus.stream()
				.filter(cmd -> !cmd.isIncludeAllServers())
				.map(IRegistrableMessageContextMenu::getRegisterName)
				.toList();
	}
	
	public void addCommand(@NonNull JDA jda, long guildId, @NonNull String value){
		registrableMessageContextMenus.stream()
				.filter(cmd -> Objects.equals(cmd.getRegisterName(), value))
				.forEach(cmd -> jda.getGuildById(guildId).upsertCommand(cmd.getDefinition(getLocalizedFunction())).submit());
	}
	
	public void deleteCommand(@NonNull JDA jda, long guildId, @NonNull String value){
		registrableMessageContextMenus.stream()
				.filter(cmd -> Objects.equals(cmd.getRegisterName(), value))
				.forEach(cmd -> jda.getGuildById(guildId).deleteCommandById(cmd.getDefinition(getLocalizedFunction()).getName()).submit());
	}
}
