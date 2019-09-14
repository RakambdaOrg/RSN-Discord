package fr.mrcraftcod.gunterdiscord.settings.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.dv8tion.jda.api.entities.Message;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Created by mrcraftcod (MrCraftCod - zerderr@gmail.com) on 2019-06-23.
 *
 * @author Thomas Couchoud
 * @since 2019-06-23
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageConfiguration{
	@JsonProperty("channel")
	private ChannelConfiguration channel;
	@JsonProperty("messageId")
	private long messageId;
	
	public MessageConfiguration(){
	}
	
	public MessageConfiguration(@Nonnull final Message message){
		this(message.getChannel().getIdLong(), message.getIdLong());
	}
	
	private MessageConfiguration(final long channelId, final long messageId){
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
		return this.getMessage().map(Message::getContentRaw).orElse("<Unknown message>") + " (" + (this.getChannel()) + ")";
	}
	
	@Nonnull
	public Optional<Message> getMessage(){
		return this.getChannel().getChannel().map(c -> c.getHistoryAround(this.getMessageId(), 1).complete().getMessageById(this.getMessageId()));
	}
	
	@Nonnull
	public ChannelConfiguration getChannel(){
		return this.channel;
	}
	
	public long getMessageId(){
		return this.messageId;
	}
	
	private void setChannel(final long channelId){
		this.channel.setChannel(channelId);
	}
	
	public void setMessage(@Nonnull final Message message){
		this.setMessage(message.getIdLong());
		this.setChannel(message.getChannel().getIdLong());
	}
	
	private void setMessage(final long messageId){
		this.messageId = messageId;
	}
}
