package fr.rakambda.rsndiscord.spring.event;

import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import fr.rakambda.rsndiscord.spring.storage.entity.ChannelEntity;
import fr.rakambda.rsndiscord.spring.storage.repository.ChannelRepository;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Pattern;
import static fr.rakambda.rsndiscord.spring.storage.entity.ChannelType.SCAN_LINK_REPLACEMENT;

@Component
@Slf4j
public class FixLinksEventListener extends ListenerAdapter{
	private static final Map<Pattern, String> PATTERNS = Map.of(
			Pattern.compile("https://(www\\.)?(?<domain>instagram.com)/\\S*"), "ddinstagram.com",
			Pattern.compile("https://(www\\.)?(?<domain>twitter.com)/\\S*"), "fxtwitter.com",
			Pattern.compile("https://(www\\.)?(?<domain>tiktok.com)/\\S*"), "tiktxk.com"
	);
	
	private final ChannelRepository channelRepository;
	
	@Autowired
	public FixLinksEventListener(ChannelRepository channelRepository){
		this.channelRepository = channelRepository;
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event){
		if(shouldHandleEvent(event.getChannel())){
			handleMessage(event.getMessage());
		}
	}
	
	@Override
	public void onMessageUpdate(MessageUpdateEvent event){
		if(shouldHandleEvent(event.getChannel())){
			handleMessage(event.getMessage());
		}
	}
	
	private boolean shouldHandleEvent(@NotNull MessageChannelUnion channel){
		if(!channel.getType().isGuild()){
			return false;
		}
		
		var guildId = channel.asGuildMessageChannel().getGuild().getIdLong();
		var channelIds = channelRepository.findAllByGuildIdAndType(guildId, SCAN_LINK_REPLACEMENT).stream()
				.map(ChannelEntity::getChannelId)
				.toList();
		
		if(channel.getType().isThread()){
			var parentChannel = channel.asThreadChannel().getParentChannel();
			if(channelIds.contains(parentChannel.getIdLong())){
				return true;
			}
		}
		
		return channelIds.contains(channel.getIdLong());
	}
	
	private void handleMessage(@NotNull Message message){
		log.debug("Handling message {} for link replacement", message);
		
		var links = extractFixedLinks(message.getContentRaw());
		if(!links.isEmpty()){
			JDAWrappers.reply(message, String.join("\n", links)).submit();
		}
	}
	
	@NotNull
	public Collection<String> extractFixedLinks(@NotNull String content){
		return PATTERNS.entrySet().stream()
				.flatMap(patternEntry -> {
					var patternLinks = new LinkedList<String>();
					var matcher = patternEntry.getKey().matcher(content);
					while(matcher.find()){
						var link = new StringBuilder(matcher.group(0));
						var relativeStart = matcher.start("domain") - matcher.start();
						var relativeEnd = matcher.end("domain") - matcher.start();
						
						link.delete(relativeStart, relativeEnd);
						link.insert(relativeStart, patternEntry.getValue());
						
						patternLinks.add(link.toString());
					}
					return patternLinks.stream();
				})
				.distinct()
				.toList();
	}
}
