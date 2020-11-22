package fr.raksrinana.rsndiscord.modules.settings.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.modules.settings.IAtomicConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import static java.util.Optional.ofNullable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class MessageConfiguration implements IAtomicConfiguration{
	@JsonProperty("channel")
	private ChannelConfiguration channel;
	@JsonProperty("messageId")
	@Setter
	private long messageId;
	
	public MessageConfiguration(@NonNull final Message message){
		this(message.getChannel().getIdLong(), message.getIdLong());
	}
	
	public MessageConfiguration(final long channelId, final long messageId){
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
		return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
				.append("channel", channel)
				.append("messageId", messageId)
				.toString();
	}
	
	@Override
	public boolean shouldBeRemoved(){
		return this.getChannel().shouldBeRemoved() || this.getChannel().getChannel()
				.map(channel -> channel.retrieveMessageById(getMessageId()).submit()
						.thenApply(m -> false)
						.exceptionally(throwable -> throwable instanceof ErrorResponseException
								&& ((ErrorResponseException) throwable).getErrorResponse() == ErrorResponse.UNKNOWN_MESSAGE))
				.map(future -> {
					try{
						return future.get();
					}
					catch(InterruptedException | ExecutionException e){
						Log.getLogger(null).error("Failed to get message", e);
					}
					return null;
				}).orElse(false);
	}
	
	@NonNull
	public Optional<Message> getMessage(){
		return this.getChannel().getChannel()
				.map(channel -> channel.retrieveMessageById(getMessageId()).submit()
						.exceptionally(throwable -> null))
				.flatMap(future -> {
					try{
						return ofNullable(future.get());
					}
					catch(InterruptedException | ExecutionException e){
						Log.getLogger(null).error("Failed to get message from configuration", e);
					}
					return Optional.empty();
				});
	}
	
	public void setMessage(@NonNull final Message message){
		this.setMessageId(message.getIdLong());
		this.setChannel(message.getChannel().getIdLong());
	}
	
	private void setChannel(final long channelId){
		this.channel.setChannelId(channelId);
	}
}
