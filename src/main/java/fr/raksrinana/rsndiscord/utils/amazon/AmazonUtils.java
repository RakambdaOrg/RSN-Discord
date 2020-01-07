package fr.raksrinana.rsndiscord.utils.amazon;

import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.AmazonTrackingConfiguration;
import fr.raksrinana.rsndiscord.settings.guild.WaitingReactionMessageConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.log.Log;
import fr.raksrinana.rsndiscord.utils.reaction.ReactionTag;
import fr.raksrinana.rsndiscord.utils.reaction.ReactionUtils;
import lombok.NonNull;
import org.jsoup.Jsoup;
import java.awt.Color;
import java.net.URL;
import java.text.MessageFormat;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class AmazonUtils{
	private final static String nameElementId = "productTitle";
	private final static String priceElementId = "priceblock_ourprice";
	private final static String imageThreshold = ".imgTagWrapper";
	
	public static Optional<AmazonProduct> getProduct(@NonNull URL url){
		try{
			final var root = Jsoup.connect(url.toString()).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36").get();
			final var name = root.getElementById(nameElementId).text();
			final var rawPrice = root.getElementById(priceElementId).text().replace(",", ".");
			final var price = Double.parseDouble(rawPrice.substring(0, rawPrice.length() - 2));
			final var product = new AmazonProduct(url, price);
			product.setRawPrice(rawPrice);
			product.setName(name);
			product.setImageUrl(new URL(root.select(imageThreshold).first().child(0).absUrl("data-old-hires")));
			return Optional.of(product);
		}
		catch(Exception e){
			Log.getLogger(null).error("Failed to get amazon product infos for {}", url, e);
		}
		return Optional.empty();
	}
	
	public static void sendMessage(@NonNull AmazonTrackingConfiguration tracking, @NonNull AmazonProduct product){
		tracking.getNotificationChannel().getChannel().flatMap(channel -> tracking.getUser().getUser().map(user -> {
			final var builder = Utilities.buildEmbed(user, Color.GREEN, "Price changed", product.getUrl().toString());
			builder.setTimestamp(ZonedDateTime.now());
			product.fillEmbed(builder);
			return Actions.sendMessage(channel, MessageFormat.format("{0}", user.getAsMention()), builder.build());
		})).ifPresent(future -> future.thenAccept(message -> {
			Actions.addReaction(message, BasicEmotes.CROSS_NO.getValue());
			final var settings = Settings.get(message.getGuild());
			settings.getMessagesAwaitingReaction(ReactionTag.AMAZON_TRACKER).stream().filter(reaction -> Objects.equals(reaction.getData().get(ReactionUtils.USER_ID_KEY), Long.toString(tracking.getUser().getUserId()))).filter(reaction -> Objects.equals(reaction.getData().get(ReactionUtils.URL_KEY), tracking.getUrl().toString())).forEach(settings::removeMessagesAwaitingReaction);
			settings.addMessagesAwaitingReaction(new WaitingReactionMessageConfiguration(message, ReactionTag.AMAZON_TRACKER, Map.of(ReactionUtils.USER_ID_KEY, Long.toString(tracking.getUser().getUserId()), ReactionUtils.URL_KEY, tracking.getUrl().toString())));
		}));
	}
}
