package fr.raksrinana.rsndiscord.event;

import fr.raksrinana.rsndiscord.api.uselessfacts.UselessFactsApi;
import fr.raksrinana.rsndiscord.log.LogContext;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import static java.awt.Color.GREEN;

@EventListener
@Log4j2
public class PrivateMessageEventListener extends ListenerAdapter{
	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event){
		super.onMessageReceived(event);
		if(!event.isFromType(ChannelType.PRIVATE)){
			return;
		}
		
		var author = event.getAuthor();
		
		try(var ignored = LogContext.empty().with(author)){
			var self = event.getJDA().getSelfUser();
			if(author.isBot()){
				return;
			}
			
			log.info("Received private message from {}: {}", author, event.getMessage());
			UselessFactsApi.getFact().ifPresentOrElse(fact -> {
				log.debug("Sending random fact: {}", fact);
				var builder = new EmbedBuilder().setAuthor(self.getName(), null, self.getAvatarUrl())
						.setColor(GREEN)
						.setTitle("Random fact");
				fact.fillEmbed(builder);
				
				JDAWrappers.message(event, "I don't really know what to answer, but here's a random fact for you")
						.embed(builder.build())
						.submit();
			}, () -> JDAWrappers.message(event, "I just farted").submit());
		}
		catch(Exception e){
			log.error("Error private message from {}", author, e);
		}
	}
}
