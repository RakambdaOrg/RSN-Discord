package fr.raksrinana.rsndiscord.event;

import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@EventListener
public class JoinLeaveListener extends ListenerAdapter{
	@Override
	public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event){
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
					
					JDAWrappers.message(channel, embed).submit();
				});
	}
	
	@Override
	public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event){
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
							.addField("Mention", user.getAsMention(), true)
							.build();
					
					JDAWrappers.message(channel, embed).submit();
				});
	}
	
	@NotNull
	private Optional<String> getImage(@NotNull Set<String> set){
		if(set.isEmpty()){
			return Optional.empty();
		}
		
		var random = ThreadLocalRandom.current().nextInt(set.size());
		return set.stream()
				.skip(random)
				.findFirst();
	}
}
