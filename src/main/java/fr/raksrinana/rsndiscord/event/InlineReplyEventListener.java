package fr.raksrinana.rsndiscord.event;

import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.Getter;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import static java.util.Objects.isNull;

@EventListener
@Getter
public class InlineReplyEventListener extends ListenerAdapter{
	@Override
	public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event){
		super.onGuildMessageReceived(event);
		try{
			var message = event.getMessage();
			var author = event.getAuthor();
			
			if(message.getType() != MessageType.INLINE_REPLY || author.isBot()){
				return;
			}
			
			var reference = message.getReferencedMessage();
			if(isNull(reference)){
				return;
			}
			
			if(Objects.equals(reference.getAuthor(), Main.getJda().getSelfUser())){
				var original = Arrays.stream(reference.getContentRaw().split("\n"))
						.filter(line -> !line.startsWith("__**EP "))
						.map(line -> line.split(" ", 2)[0])
						.collect(Collectors.toList());
				var received = Arrays.stream(message.getContentRaw().split("\n")).collect(Collectors.toList());
				
				if(original.size() == received.size()){
					var content = event.getAuthor().getAsMention() + " replied:\n\n" +
							IntStream.range(0, original.size())
									.mapToObj(index -> original.get(index) + " " + received.get(index))
									.collect(Collectors.joining("\n"));
					
					JDAWrappers.reply(reference, content).submit()
							.thenAccept(sent -> JDAWrappers.delete(message).submit());
				}
			}
		}
		catch(Exception e){
			Log.getLogger(event.getGuild()).error("Error handling message", e);
		}
	}
}
