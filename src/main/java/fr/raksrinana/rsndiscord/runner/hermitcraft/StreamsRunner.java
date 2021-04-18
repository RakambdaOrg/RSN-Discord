package fr.raksrinana.rsndiscord.runner.hermitcraft;

import fr.raksrinana.rsndiscord.api.hermitcraft.HermitcraftApi;
import fr.raksrinana.rsndiscord.api.hermitcraft.data.Hermit;
import fr.raksrinana.rsndiscord.runner.IScheduledRunner;
import fr.raksrinana.rsndiscord.runner.ScheduledRunner;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;
import java.awt.Color;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.util.concurrent.TimeUnit.MINUTES;

@ScheduledRunner
public class StreamsRunner implements IScheduledRunner{
	private final JDA jda;
	private final Set<String> hermitAlreadyNotified;
	
	public StreamsRunner(@NotNull JDA jda){
		this.jda = jda;
		hermitAlreadyNotified = new HashSet<>();
	}
	
	@Override
	public void execute(){
		HermitcraftApi.getHermits().ifPresent(hermits -> {
			for(var hermit : hermits){
				if(hermit.isLive()){
					if(!hermitAlreadyNotified.contains(hermit.getChannelId())){
						hermitAlreadyNotified.add(hermit.getChannelId());
						jda.getGuilds().forEach(guild -> Settings.get(guild)
								.getHermitcraftConfiguration()
								.getStreamingNotificationChannel()
								.flatMap(ChannelConfiguration::getChannel)
								.ifPresent(channel -> sendStream(hermit, channel)));
					}
				}
				else{
					hermitAlreadyNotified.remove(hermit.getChannelId());
				}
			}
		});
	}
	
	@Override
	public long getDelay(){
		return 2;
	}
	
	@NotNull
	@Override
	public String getName(){
		return "Hermitcraft stream fetcher";
	}
	
	@Override
	public long getPeriod(){
		return 5;
	}
	
	@NotNull
	@Override
	public TimeUnit getPeriodUnit(){
		return MINUTES;
	}
	
	private void sendStream(@NotNull Hermit hermit, @NotNull TextChannel channel){
		var selfUser = jda.getSelfUser();
		var url = hermit.getLiveUrl().map(URL::toString).orElse(null);
		
		var embed = new EmbedBuilder().setAuthor(selfUser.getName(), null, selfUser.getAvatarUrl())
				.setColor(Color.GREEN)
				.setTitle(translate(channel.getGuild(), "hermitcraft.live", hermit.getDisplayName()), url)
				.addField(translate(channel.getGuild(), "hermitcraft.hermit"), hermit.getDisplayName(), true)
				.setThumbnail(hermit.getProfilePicture().toString())
				.build();
		JDAWrappers.message(channel, embed).submit();
	}
}
