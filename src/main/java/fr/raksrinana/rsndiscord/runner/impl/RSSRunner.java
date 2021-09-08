package fr.raksrinana.rsndiscord.runner.impl;

import com.apptastic.rssreader.DateTime;
import com.apptastic.rssreader.Item;
import com.apptastic.rssreader.RssReader;
import fr.raksrinana.rsndiscord.runner.api.IScheduledRunner;
import fr.raksrinana.rsndiscord.runner.api.ScheduledRunner;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.impl.guild.RSSConfiguration;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import static java.util.concurrent.TimeUnit.HOURS;

@ScheduledRunner
public class RSSRunner implements IScheduledRunner{
	private final RssReader reader;
	
	public RSSRunner(){
		reader = new RssReader();
	}
	
	@Override
	public void executeGlobal(@NotNull JDA jda) throws Exception{
	}
	
	@Override
	public void executeGuild(@NotNull Guild guild) throws Exception{
		var rssConfiguration = Settings.get(guild).getRss();
		rssConfiguration.getChannel()
				.flatMap(ChannelConfiguration::getChannel)
				.ifPresent(channel -> rssConfiguration.getFeeds().forEach(url -> processFeed(rssConfiguration, channel, url)));
	}
	
	private void processFeed(@NotNull RSSConfiguration rssConfiguration, @NotNull TextChannel channel, @NotNull URL url){
		var lastDate = rssConfiguration.getLastPublicationDate(url.toString());
		reader.readAsync(url.toString())
				.thenAccept(items -> {
					var newItems = items.sorted()
							.filter(item -> lastDate.map(date -> date < item.getPubDate().map(DateTime::toEpochMilli).orElse(0L)).orElse(true))
							.collect(Collectors.toList());
					
					newItems.forEach(item -> publish(channel, item));
					
					newItems.stream()
							.flatMap(item -> item.getPubDate().map(DateTime::toEpochMilli).stream())
							.mapToLong(l -> l)
							.max()
							.ifPresent(maxDate -> rssConfiguration.setLastPublicationDate(url.toString(), maxDate));
				});
	}
	
	private void publish(@NotNull TextChannel channel, @NotNull Item item){
		var builder = new EmbedBuilder();
		builder.setTitle("New RSS entry in " + item.getChannel().getTitle());
		item.getDescription().ifPresent(builder::setDescription);
		item.getAuthor().ifPresent(builder::setAuthor);
		item.getCategory().ifPresent(category -> builder.addField("Category", category, true));
		item.getLink().ifPresent(link -> builder.addField("Link", link, true));
		item.getPubDateZonedDateTime().ifPresent(builder::setTimestamp);
		
		JDAWrappers.message(channel, builder.build()).submit();
	}
	
	@Override
	public long getDelay(){
		return 0;
	}
	
	@Override
	public long getPeriod(){
		return 1;
	}
	
	@Override
	@NotNull
	public String getName(){
		return "rss";
	}
	
	@Override
	@NotNull
	public TimeUnit getPeriodUnit(){
		return HOURS;
	}
}
