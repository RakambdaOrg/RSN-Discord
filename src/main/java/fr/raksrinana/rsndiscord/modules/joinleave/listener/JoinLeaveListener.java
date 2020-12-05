package fr.raksrinana.rsndiscord.modules.joinleave.listener;

import fr.raksrinana.rsndiscord.listeners.EventListener;
import fr.raksrinana.rsndiscord.modules.settings.Settings;
import fr.raksrinana.rsndiscord.modules.settings.types.ChannelConfiguration;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@EventListener
public class JoinLeaveListener extends ListenerAdapter{
	@Override
	public void onGuildMemberRemove(@NonNull GuildMemberRemoveEvent event){
		super.onGuildMemberRemove(event);
		var guild = event.getGuild();
		var user = event.getUser();
		var joinLeaveConfiguration = Settings.get(guild).getJoinLeaveConfiguration();
		
		joinLeaveConfiguration.getChannel()
				.flatMap(ChannelConfiguration::getChannel)
				.ifPresent(channel -> {
					var title = translate(guild, "joinLeave.leave.title", user.getName() + "#" + user.getDiscriminator());
					var description = translate(guild, "joinLeave.leave.description", guild.getMemberCount());
					var image = getImage(joinLeaveConfiguration.getLeaveImages()).orElse(null);
					
					var embed = new EmbedBuilder()
							.setTitle(title)
							.setDescription(description)
							.setThumbnail(user.getAvatarUrl())
							.setImage(image)
							.build();
					
					channel.sendMessage(embed).submit();
				});
	}
	
	@Override
	public void onGuildMemberJoin(@NonNull final GuildMemberJoinEvent event){
		super.onGuildMemberJoin(event);
		var guild = event.getGuild();
		var user = event.getUser();
		var joinLeaveConfiguration = Settings.get(guild).getJoinLeaveConfiguration();
		
		joinLeaveConfiguration.getChannel()
				.flatMap(ChannelConfiguration::getChannel)
				.ifPresent(channel -> {
					var title = translate(guild, "joinLeave.join.title", user.getName() + "#" + user.getDiscriminator());
					var description = translate(guild, "joinLeave.join.description", guild.getMemberCount());
					var image = getImage(joinLeaveConfiguration.getJoinImages()).orElse(null);
					
					var embed = new EmbedBuilder()
							.setTitle(title)
							.setDescription(description)
							.setThumbnail(user.getAvatarUrl())
							.setImage(image)
							.build();
					
					channel.sendMessage(embed).submit();
				});
	}
	
	private Optional<String> getImage(Set<String> set){
		if(set.isEmpty()){
			return Optional.empty();
		}
		
		var random = ThreadLocalRandom.current().nextInt(set.size());
		return set.stream()
				.skip(random)
				.findFirst();
	}
}
