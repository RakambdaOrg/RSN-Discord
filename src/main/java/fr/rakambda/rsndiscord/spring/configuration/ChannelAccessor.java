package fr.rakambda.rsndiscord.spring.configuration;

import fr.rakambda.rsndiscord.spring.storage.entity.ChannelEntity;
import fr.rakambda.rsndiscord.spring.storage.entity.ChannelType;
import fr.rakambda.rsndiscord.spring.storage.repository.ChannelRepository;
import jakarta.transaction.Transactional;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jspecify.annotations.NonNull;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class ChannelAccessor implements IConfigurationAccessor{
	private final ChannelRepository channelRepository;
	private final ChannelType channelType;
	
	public ChannelAccessor(@NonNull ChannelRepository channelRepository, @NonNull ChannelType channelType){
		this.channelRepository = channelRepository;
		this.channelType = channelType;
	}
	
	@Override
	@Transactional
	public boolean add(@NonNull JDA jda, long guildId, @NonNull String value){
		channelRepository.save(ChannelEntity.builder()
				.channelId(Long.parseLong(value))
				.type(channelType)
				.guildId(guildId)
				.build());
		return true;
	}
	
	@Override
	@Transactional
	public boolean remove(@NonNull JDA jda, long guildId, @NonNull String value){
		return channelRepository.deleteAllByGuildIdAndTypeAndChannelId(guildId, channelType, Long.parseLong(value)) > 0;
	}
	
	@Override
	@Transactional
	public boolean reset(@NonNull JDA jda, long guildId){
		channelRepository.deleteAllByGuildIdAndType(guildId, channelType);
		return true;
	}
	
	@Override
	@NonNull
	public Optional<MessageEmbed> show(long guildId){
		var value = channelRepository.findAllByGuildIdAndType(guildId, channelType).stream()
				.map(ChannelEntity::getChannelId)
				.map(Objects::toString)
				.collect(Collectors.joining(", "));
		
		var builder = new EmbedBuilder()
				.setTitle("Configuration value")
				.addField("Value", value, false);
		
		return Optional.of(builder.build());
	}
}
