package fr.raksrinana.rsndiscord.runners;

import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.amazon.AmazonUtils;
import fr.raksrinana.rsndiscord.utils.log.Log;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.JDA;
import java.util.concurrent.TimeUnit;

public class AmazonPriceCheckerScheduledRunner implements ScheduledRunner{
	@Getter
	private final JDA jda;
	
	public AmazonPriceCheckerScheduledRunner(JDA jda){
		this.jda = jda;
		Log.getLogger(null).info("Creating Amazon price scheduler runner");
	}
	
	@Override
	public void run(){
		Log.getLogger(null).info("Starting Amazon price scheduler runner");
		this.getJda().getGuilds().forEach(guild -> {
			final var settings = Settings.get(guild);
			settings.getAmazonTrackings().forEach(tracking -> AmazonUtils.getProduct(tracking.getUrl()).ifPresent(product -> {
				if(product.getPrice() != tracking.getLastPrice()){
					tracking.setLastPrice(product.getPrice());
					AmazonUtils.sendMessage(tracking, product);
				}
			}));
		});
		Log.getLogger(null).info("Amazon price scheduler runner done");
	}
	
	@Override
	public long getDelay(){
		return 0;
	}
	
	@Override
	public long getPeriod(){
		return 6;
	}
	
	@NonNull
	@Override
	public TimeUnit getPeriodUnit(){
		return TimeUnit.HOURS;
	}
}
