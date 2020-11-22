package fr.raksrinana.rsndiscord.modules.hermitcraft.runner;

import fr.raksrinana.rsndiscord.modules.hermitcraft.HermitcraftUtils;
import fr.raksrinana.rsndiscord.modules.hermitcraft.data.Hermit;
import fr.raksrinana.rsndiscord.modules.settings.Settings;
import fr.raksrinana.rsndiscord.modules.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.runner.IScheduledRunner;
import fr.raksrinana.rsndiscord.runner.ScheduledRunner;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
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
	
	public StreamsRunner(@NonNull JDA jda){
		this.jda = jda;
		this.hermitAlreadyNotified = new HashSet<>();
	}
	
	@Override
	public void execute(){
		HermitcraftUtils.getHermits().ifPresent(hermits -> {
			for(var hermit : hermits){
				if(hermit.isLive()){
					if(!this.hermitAlreadyNotified.contains(hermit.getChannelId())){
						this.hermitAlreadyNotified.add(hermit.getChannelId());
						this.jda.getGuilds().forEach(guild -> Settings.get(guild)
								.getHermitcraftConfiguration()
								.getStreamingNotificationChannel()
								.flatMap(ChannelConfiguration::getChannel)
								.ifPresent(channel -> sendStream(hermit, channel)));
					}
				}
				else{
					this.hermitAlreadyNotified.remove(hermit.getChannelId());
				}
			}
		});
	}
	
	@Override
	public long getDelay(){
		return 2;
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Hermitcraft stream fetcher";
	}
	
	@Override
	public long getPeriod(){
		return 5;
	}
	
	@NonNull
	@Override
	public TimeUnit getPeriodUnit(){
		return MINUTES;
	}
	
	private void sendStream(Hermit hermit, TextChannel channel){
		var selfUser = jda.getSelfUser();
		var url = hermit.getLiveUrl().map(URL::toString).orElse(null);
		
		var embed = new EmbedBuilder().setAuthor(selfUser.getName(), null, selfUser.getAvatarUrl())
				.setColor(Color.GREEN)
				.setTitle(translate(channel.getGuild(), "hermitcraft.live", hermit.getDisplayName()), url)
				.addField(translate(channel.getGuild(), "hermitcraft.hermit"), hermit.getDisplayName(), true)
				.setThumbnail(hermit.getProfilePicture().toString())
				.build();
		channel.sendMessage(embed).submit();
	}
}
