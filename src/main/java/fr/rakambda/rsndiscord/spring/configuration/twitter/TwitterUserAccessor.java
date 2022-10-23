package fr.rakambda.rsndiscord.spring.configuration.twitter;

import fr.rakambda.rsndiscord.spring.configuration.IConfigurationAccessor;
import fr.rakambda.rsndiscord.spring.storage.entity.GuildEntity;
import fr.rakambda.rsndiscord.spring.storage.entity.TwitterEntity;
import fr.rakambda.rsndiscord.spring.storage.entity.TwitterType;
import fr.rakambda.rsndiscord.spring.storage.repository.TwitterRepository;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class TwitterUserAccessor implements IConfigurationAccessor{
	private static final TwitterType TWITTER_TYPE = TwitterType.USER;
	
	private final TwitterRepository twitterRepository;
	
	public TwitterUserAccessor(TwitterRepository twitterRepository){
		this.twitterRepository = twitterRepository;
	}
	
	@Override
	@NotNull
	public String getName(){
		return "twitter.query.users";
	}
	
	@Override
	public boolean add(long guildId, @NotNull String value){
		twitterRepository.save(TwitterEntity.builder()
				.search(value.trim())
				.type(TWITTER_TYPE)
				.guild(GuildEntity.builder()
						.id(guildId)
						.build())
				.build());
		return true;
	}
	
	@Override
	public boolean remove(long guildId, @NotNull String value){
		return twitterRepository.deleteAllByGuild_IdAndTypeAndSearch(guildId, TWITTER_TYPE, value.trim());
	}
	
	@Override
	public boolean reset(long guildId){
		return twitterRepository.deleteAllByGuild_IdAndType(guildId, TWITTER_TYPE);
	}
	
	@Override
	@NotNull
	public Optional<MessageEmbed> show(long guildId){
		var value = twitterRepository.findAllByGuild_IdAndType(guildId, TWITTER_TYPE).stream()
				.map(t -> "%s: %s".formatted(t.getType(), t.getSearch()))
				.map(Objects::toString)
				.collect(Collectors.joining(", "));
		
		var builder = new EmbedBuilder()
				.setTitle("Configuration value")
				.addField("Value", value, false);
		
		return Optional.of(builder.build());
	}
}
