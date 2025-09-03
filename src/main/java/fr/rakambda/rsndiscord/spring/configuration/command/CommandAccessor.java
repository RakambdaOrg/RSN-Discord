package fr.rakambda.rsndiscord.spring.configuration.command;

import fr.rakambda.rsndiscord.spring.configuration.IConfigurationAccessor;
import fr.rakambda.rsndiscord.spring.interaction.InteractionType;
import fr.rakambda.rsndiscord.spring.interaction.context.message.MessageContextMenuService;
import fr.rakambda.rsndiscord.spring.interaction.exception.OperationNotSupportedException;
import fr.rakambda.rsndiscord.spring.interaction.slash.SlashCommandService;
import fr.rakambda.rsndiscord.spring.storage.entity.CommandEntity;
import fr.rakambda.rsndiscord.spring.storage.repository.CommandRepository;
import jakarta.transaction.Transactional;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class CommandAccessor implements IConfigurationAccessor{
	private final CommandRepository commandRepository;
	private final SlashCommandService slashCommandService;
	private final MessageContextMenuService messageContextMenuService;
	
	@Autowired
	public CommandAccessor(CommandRepository commandRepository,
			@Lazy SlashCommandService slashCommandService,
			@Lazy MessageContextMenuService messageContextMenuService){
		this.commandRepository = commandRepository;
		this.slashCommandService = slashCommandService;
		this.messageContextMenuService = messageContextMenuService;
	}
	
	@Override
	@Transactional
	public boolean add(@NonNull JDA jda, long guildId, @NonNull String value) throws OperationNotSupportedException{
		commandRepository.save(CommandEntity.builder()
				.name(value)
				.guildId(guildId)
				.enabled(true)
				.build());
		
		if(value.startsWith(InteractionType.SLASH.getPrefix())){
			slashCommandService.addCommand(jda, guildId, value);
		}
		else if(value.startsWith(InteractionType.CONTEXT_MESSAGE.getPrefix())){
			messageContextMenuService.addCommand(jda, guildId, value);
		}
		return true;
	}
	
	@Override
	@Transactional
	public boolean remove(@NonNull JDA jda, long guildId, @NonNull String value) throws OperationNotSupportedException{
		var result = commandRepository.deleteAllByGuildIdAndName(guildId, value) > 0;
		
		if(result){
			if(value.startsWith(InteractionType.SLASH.getPrefix())){
				slashCommandService.deleteCommand(jda, guildId, value);
			}
			else if(value.startsWith(InteractionType.CONTEXT_MESSAGE.getPrefix())){
				messageContextMenuService.deleteCommand(jda, guildId, value);
			}
		}
		return result;
	}
	
	@Override
	@NonNull
	public Optional<MessageEmbed> show(long guildId) throws OperationNotSupportedException{
		var value = commandRepository.findByGuildId(guildId).stream()
				.map(CommandEntity::getName)
				.map(Objects::toString)
				.collect(Collectors.joining(", "));
		
		var builder = new EmbedBuilder()
				.setTitle("Configuration value")
				.addField("Value", value, false);
		
		return Optional.of(builder.build());
	}
	
	@Override
	@NonNull
	public String getName(){
		return "commands";
	}
	
	@Override
	@NonNull
	public Collection<Command.Choice> autoComplete(@NonNull CommandAutoCompleteInteractionEvent event){
		return getCommandsStartingWith(event.getFocusedOption().getValue())
				.limit(OptionData.MAX_CHOICES)
				.sorted()
				.map(name -> new Command.Choice(name, name))
				.toList();
	}
	
	@NonNull
	private Stream<String> getCommandsStartingWith(@NonNull String value){
		var commands = Stream.concat(slashCommandService.getRegistrableCommandNames().stream(), messageContextMenuService.getRegistrableContextMenu().stream());
		if(value.isBlank()){
			return commands;
		}
		return commands.filter(name -> name.startsWith(value));
	}
}
