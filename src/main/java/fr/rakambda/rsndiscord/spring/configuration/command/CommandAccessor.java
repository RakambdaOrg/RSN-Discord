package fr.rakambda.rsndiscord.spring.configuration.command;

import fr.rakambda.rsndiscord.spring.configuration.IConfigurationAccessor;
import fr.rakambda.rsndiscord.spring.interaction.exception.OperationNotSupportedException;
import fr.rakambda.rsndiscord.spring.interaction.slash.SlashCommandService;
import fr.rakambda.rsndiscord.spring.storage.entity.CommandEntity;
import fr.rakambda.rsndiscord.spring.storage.repository.CommandRepository;
import jakarta.transaction.Transactional;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CommandAccessor implements IConfigurationAccessor{
	private final CommandRepository commandRepository;
	private final SlashCommandService slashCommandService;
	
	@Autowired
	public CommandAccessor(CommandRepository commandRepository, SlashCommandService slashCommandService){
		this.commandRepository = commandRepository;
        this.slashCommandService = slashCommandService;
    }
	
	@Override
	@Transactional
	public boolean add(long guildId, @NotNull String value) throws OperationNotSupportedException{
		commandRepository.save(CommandEntity.builder()
				.name(value)
				.guildId(guildId)
				.enabled(true)
				.build());
		slashCommandService.addCommand(guildId, value);
		return true;
	}
	
	@Override
	@Transactional
	public boolean remove(long guildId, @NotNull String value) throws OperationNotSupportedException{
		var result = commandRepository.deleteAllByGuildIdAndName(guildId, value) > 0;
		if(result){
			slashCommandService.deleteCommand(guildId, value);
		}
		return result;
	}
	
	@Override
	@NotNull
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
	@NotNull
	public String getName(){
		return "commands";
	}
}
