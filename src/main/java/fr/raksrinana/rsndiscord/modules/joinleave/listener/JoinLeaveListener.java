package fr.raksrinana.rsndiscord.modules.joinleave.listener;

import fr.raksrinana.rsndiscord.listeners.EventListener;
import fr.raksrinana.rsndiscord.modules.settings.Settings;
import fr.raksrinana.rsndiscord.modules.settings.types.ChannelConfiguration;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@EventListener
public class JoinLeaveListener extends ListenerAdapter{
	private static final List<String> LEAVE_IMAGES = List.of(
			"https://media1.tenor.com/images/5221203dc94b7a099b430e44a41e6c73/tenor.gif?itemid=5047945",
			"https://media1.tenor.com/images/40a39123002fcbac2ff9870fb34417e7/tenor.gif?itemid=18024932",
			"https://media1.tenor.com/images/4e3659e170d40a413d11f1acbc9809ea/tenor.gif?itemid=15655147",
			"https://media1.tenor.com/images/7ab5c1c6550b1244dfaab01d9caa6e12/tenor.gif?itemid=12791657",
			"https://media1.tenor.com/images/c9c25f239ccb87c6d76286f2761b2a0d/tenor.gif?itemid=12406587"
	);
	private static final List<String> JOIN_IMAGES = List.of(
			"https://media1.tenor.com/images/fd4130d1f5aede7f23fbe4fcb844854e/tenor.gif?itemid=17430483"
	);
	
	@Override
	public void onGuildMemberRemove(@NonNull GuildMemberRemoveEvent event){
		super.onGuildMemberRemove(event);
		var guild = event.getGuild();
		var user = event.getUser();
		Settings.get(guild).getJoinLeaveConfiguration().getChannel()
				.flatMap(ChannelConfiguration::getChannel)
				.ifPresent(channel -> {
					var title = translate(guild, "joinLeave.leave.title", user.getName() + "#" + user.getDiscriminator());
					var description = translate(guild, "joinLeave.leave.description", guild.getMemberCount());
					var image = LEAVE_IMAGES.get(ThreadLocalRandom.current().nextInt(LEAVE_IMAGES.size()));
					
					var embed = new EmbedBuilder()
							.setAuthor(title, null, user.getAvatarUrl())
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
		Settings.get(guild).getJoinLeaveConfiguration().getChannel()
				.flatMap(ChannelConfiguration::getChannel)
				.ifPresent(channel -> {
					var title = translate(guild, "joinLeave.join.title", user.getName() + "#" + user.getDiscriminator());
					var description = translate(guild, "joinLeave.join.description", guild.getMemberCount());
					var image = JOIN_IMAGES.get(ThreadLocalRandom.current().nextInt(JOIN_IMAGES.size()));
					
					var embed = new EmbedBuilder()
							.setAuthor(title, null, user.getAvatarUrl())
							.setTitle(title)
							.setDescription(description)
							.setThumbnail(user.getAvatarUrl())
							.setImage(image)
							.build();
					
					channel.sendMessage(embed).submit();
				});
	}
}
