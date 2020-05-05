package fr.raksrinana.rsndiscord.runners.hermitcraft;

import fr.raksrinana.rsndiscord.runners.ScheduledRunner;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.hermitcraft.HermitcraftUtils;
import fr.raksrinana.rsndiscord.utils.hermitcraft.data.Hermit;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import java.awt.Color;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class FetchHermitcraftStreamsScheduledRunner implements ScheduledRunner{
	private final JDA jda;
	private final Set<String> hermitAlreadyNotified;
	
	public FetchHermitcraftStreamsScheduledRunner(@NonNull JDA jda){
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
		return 7;
	}
	
	@NonNull
	@Override
	public TimeUnit getPeriodUnit(){
		return TimeUnit.MINUTES;
	}
	
	private void sendStream(Hermit hermit, TextChannel channel){
		EmbedBuilder embed = Utilities.buildEmbed(this.jda.getSelfUser(), Color.GREEN, hermit.getDisplayName() + " is live", hermit.getLiveUrl().map(URL::toString).orElse(null));
		embed.addField("Hermit", hermit.getDisplayName(), true);
		embed.setThumbnail(hermit.getProfilePicture().toString());
		Actions.sendMessage(channel, "", embed.build());
	}
}