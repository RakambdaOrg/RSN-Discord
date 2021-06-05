package fr.raksrinana.rsndiscord.settings.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.settings.IAtomicConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import static java.util.Optional.ofNullable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
@Log4j2
public class MessageConfiguration implements IAtomicConfiguration{
	@JsonProperty("channel")
	private ChannelConfiguration channel;
	@JsonProperty("messageId")
	@Setter
	private long messageId;
	
	public MessageConfiguration(@NotNull Message message){
		this(message.getChannel().getIdLong(), message.getIdLong());
	}
	
	public MessageConfiguration(long channelId, long messageId){
		channel = new ChannelConfiguration(channelId);
		this.messageId = messageId;
	}
	
	@Override
	public int hashCode(){
		return new HashCodeBuilder(17, 37).append(getMessageId()).append(getChannel()).toHashCode();
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(!(o instanceof MessageConfiguration that)){
			return false;
		}
		return new EqualsBuilder().append(getMessageId(), that.getMessageId()).append(getChannel(), that.getChannel()).isEquals();
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
		return getChannel().shouldBeRemoved() || getChannel().getChannel()
				.map(channel -> channel.retrieveMessageById(getMessageId()).submit()
						.thenApply(m -> false)
						.exceptionally(throwable -> throwable instanceof ErrorResponseException
								&& ((ErrorResponseException) throwable).getErrorResponse() == ErrorResponse.UNKNOWN_MESSAGE))
				.map(future -> {
					try{
						return future.get();
					}
					catch(InterruptedException | ExecutionException e){
						log.error("Failed to get message", e);
					}
					return null;
				}).orElse(false);
	}
	
	@NotNull
	public Optional<Message> getMessage(){
		return getChannel().getChannel()
				.map(channel -> channel.retrieveMessageById(getMessageId()).submit()
						.exceptionally(throwable -> null))
				.flatMap(future -> {
					try{
						return ofNullable(future.get());
					}
					catch(InterruptedException | ExecutionException e){
						log.error("Failed to get message from configuration", e);
					}
					return Optional.empty();
				});
	}
	
	public void setMessage(@NotNull Message message){
		setMessageId(message.getIdLong());
		setChannel(message.getChannel().getIdLong());
	}
	
	private void setChannel(long channelId){
		channel.setChannelId(channelId);
	}
}
