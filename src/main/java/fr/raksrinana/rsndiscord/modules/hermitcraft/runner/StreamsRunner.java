package fr.raksrinana.rsndiscord.modules.hermitcraft.runner;

import fr.raksrinana.rsndiscord.modules.hermitcraft.HermitcraftUtils;
import fr.raksrinana.rsndiscord.modules.hermitcraft.data.Hermit;
import fr.raksrinana.rsndiscord.modules.settings.Settings;
import fr.raksrinana.rsndiscord.modules.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.runner.IScheduledRunner;
import fr.raksrinana.rsndiscord.runner.ScheduledRunner;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.Utilities;
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
			for(final var hermit : hermits){
				if(hermit.isLive()){
					if(!this.hermitAlreadyNotified.contains(hermit.getChannelId())){
						this.hermitAlreadyNotified.add(hermit.getChannelId());
						this.jda.getGuilds().forEach(guild -> Settings.get(guild).getHermitcraftConfiguration().getStreamingNotificationChannel().flatMap(ChannelConfiguration::getChannel).ifPresent(channel -> sendStream(hermit, channel)));
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
		return TimeUnit.MINUTES;
	}
	
	private void sendStream(Hermit hermit, TextChannel channel){
		EmbedBuilder embed = Utilities.buildEmbed(this.jda.getSelfUser(), Color.GREEN, translate(channel.getGuild(), "hermitcraft.live", hermit.getDisplayName()), hermit.getLiveUrl().map(URL::toString).orElse(null));
		embed.addField(translate(channel.getGuild(), "hermitcraft.hermit"), hermit.getDisplayName(), true);
		embed.setThumbnail(hermit.getProfilePicture().toString());
		Actions.sendEmbed(channel, embed.build());
	}
}
