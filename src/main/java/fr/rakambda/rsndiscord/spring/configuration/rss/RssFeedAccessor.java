package fr.rakambda.rsndiscord.spring.configuration.rss;

import fr.rakambda.rsndiscord.spring.configuration.IConfigurationAccessor;
import fr.rakambda.rsndiscord.spring.storage.entity.GuildEntity;
import fr.rakambda.rsndiscord.spring.storage.entity.RssEntity;
import fr.rakambda.rsndiscord.spring.storage.repository.RssRepository;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class RssFeedAccessor implements IConfigurationAccessor{
	private final RssRepository rssRepository;
	
	public RssFeedAccessor(@NotNull RssRepository rssRepository){
		this.rssRepository = rssRepository;
	}
	
	@Override
	@NotNull
	public String getName(){
		return "rss.feeds";
	}
	
	@Override
	public boolean add(long guildId, @NotNull String value){
		rssRepository.save(RssEntity.builder()
				.url(value.trim())
				.guild(GuildEntity.builder()
						.id(guildId)
						.build())
				.build());
		return true;
	}
	
	@Override
	public boolean remove(long guildId, @NotNull String value){
		return rssRepository.deleteAllByGuild_IdAndUrl(guildId, value.trim()) > 0;
	}
	
	@Override
	public boolean reset(long guildId){
		rssRepository.deleteAllByGuild_Id(guildId);
		return true;
	}
	
	@Override
	@NotNull
	public Optional<MessageEmbed> show(long guildId){
		var value = rssRepository.findAllByGuild_Id(guildId).stream()
				.map(RssEntity::getUrl)
				.map(Objects::toString)
				.collect(Collectors.joining(", "));
		
		var builder = new EmbedBuilder()
				.setTitle("Configuration value")
				.addField("Value", value, false);
		
		return Optional.of(builder.build());
	}
}
