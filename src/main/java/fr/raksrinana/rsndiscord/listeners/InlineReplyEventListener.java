package fr.raksrinana.rsndiscord.listeners;

import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.log.Log;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import static java.util.Objects.isNull;

@EventListener
@Getter
public class InlineReplyEventListener extends ListenerAdapter{
	@Override
	public void onGuildMessageReceived(@NonNull final GuildMessageReceivedEvent event){
		super.onGuildMessageReceived(event);
		try{
			var message = event.getMessage();
			var author = event.getAuthor();
			var channel = event.getChannel();
			
			if(message.getType() != MessageType.INLINE_REPLY || author.isBot()){
				return;
			}
			
			var reference = message.getReferencedMessage();
			if(isNull(reference)){
				return;
			}
			
			if(Objects.equals(reference.getAuthor(), Main.getJda().getSelfUser())){
				var original = Arrays.stream(reference.getContentRaw().split("\n"))
						.filter(line -> Character.isDigit(line.charAt(0)))
						.map(line -> line.split(" ", 2)[0])
						.collect(Collectors.toList());
				var received = Arrays.stream(message.getContentRaw().split("\n")).collect(Collectors.toList());
				
				if(original.size() == received.size()){
					var sb = new StringBuilder();
					for(int i = 0; i < original.size(); i++){
						sb.append(original.get(i))
								.append(" ")
								.append(received.get(i));
					}
					channel.sendMessage(sb).submit()
							.thenAccept(sent -> message.delete().submit());
				}
			}
		}
		catch(final Exception e){
			Log.getLogger(event.getGuild()).error("Error handling message", e);
		}
	}
}
