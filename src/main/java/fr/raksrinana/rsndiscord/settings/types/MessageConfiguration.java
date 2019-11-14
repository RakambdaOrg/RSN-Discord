package fr.raksrinana.rsndiscord.settings.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.log.Log;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Message;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class MessageConfiguration{
	@JsonProperty("channel")
	private ChannelConfiguration channel;
	@JsonProperty("messageId")
	private long messageId;
	
	public MessageConfiguration(@NonNull final Message message){
		this(message.getChannel().getIdLong(), message.getIdLong());
	}
	
	MessageConfiguration(final long channelId, final long messageId){
		this.channel = new ChannelConfiguration(channelId);
		this.messageId = messageId;
	}
	
	@Override
	public int hashCode(){
		return new HashCodeBuilder(17, 37).append(this.getMessageId()).append(this.getChannel()).toHashCode();
	}
	
	@Override
	public boolean equals(final Object o){
		if(this == o){
			return true;
		}
		if(!(o instanceof MessageConfiguration)){
			return false;
		}
		final var that = (MessageConfiguration) o;
		return new EqualsBuilder().append(this.getMessageId(), that.getMessageId()).append(this.getChannel(), that.getChannel()).isEquals();
	}
	
	@Override
	public String toString(){
		return "" + this.getMessageId() + '(' + this.getChannel() + ')';
	}
	
	@NonNull
	public Optional<Message> getMessage(){
		return this.getChannel().getChannel().map(channel -> Utilities.getMessageById(channel, this.getMessageId())).flatMap(future -> {
			try{
				return future.get();
			}
			catch(InterruptedException | ExecutionException e){
				Log.getLogger(null).error("Failed to get message from configuration", e);
			}
			return Optional.empty();
		});
	}
	
	public void setMessage(@NonNull final Message message){
		this.setMessage(message.getIdLong());
		this.setChannel(message.getChannel().getIdLong());
	}
	
	private void setMessage(final long messageId){
		this.messageId = messageId;
	}
	
	private void setChannel(final long channelId){
		this.channel.setChannel(channelId);
	}
}
