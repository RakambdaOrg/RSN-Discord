package fr.rakambda.rsndiscord.spring.configuration;

import fr.rakambda.rsndiscord.spring.storage.entity.ChannelEntity;
import fr.rakambda.rsndiscord.spring.storage.entity.ChannelType;
import fr.rakambda.rsndiscord.spring.storage.entity.GuildEntity;
import fr.rakambda.rsndiscord.spring.storage.repository.ChannelRepository;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class ChannelAccessor implements IConfigurationAccessor{
	private final ChannelRepository channelRepository;
	private final ChannelType channelType;
	
	public ChannelAccessor(@NotNull ChannelRepository channelRepository, @NotNull ChannelType channelType){
		this.channelRepository = channelRepository;
		this.channelType = channelType;
	}
	
	@Override
	public boolean add(long guildId, @NotNull String value){
		channelRepository.save(ChannelEntity.builder()
				.channelId(Long.parseLong(value))
				.type(channelType)
				.guild(GuildEntity.builder()
						.id(guildId)
						.build())
				.build());
		return true;
	}
	
	@Override
	public boolean remove(long guildId, @NotNull String value){
		return channelRepository.deleteAllByGuild_IdAndTypeAndChannelId(guildId, channelType, Long.parseLong(value));
	}
	
	@Override
	public boolean reset(long guildId){
		return channelRepository.deleteAllByGuild_IdAndType(guildId, channelType);
	}
	
	@Override
	@NotNull
	public Optional<MessageEmbed> show(long guildId){
		var value = channelRepository.findAllByGuild_IdAndType(guildId, channelType).stream()
				.map(ChannelEntity::getChannelId)
				.map(Objects::toString)
				.collect(Collectors.joining(", "));
		
		var builder = new EmbedBuilder()
				.setTitle("Configuration value")
				.addField("Value", value, false);
		
		return Optional.of(builder.build());
	}
}
