package fr.raksrinana.rsndiscord.settings.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.dv8tion.jda.api.entities.Message;
import javax.annotation.Nonnull;

/**
 * Created by mrcraftcod (MrCraftCod - zerderr@gmail.com) on 2019-10-10.
 *
 * @author Thomas Couchoud
 * @since 2019-10-10
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TodoConfiguration{
	@JsonProperty("message")
	private MessageConfiguration message;
	@JsonProperty("deleteOnDone")
	private boolean deleteOnDone = false;
	
	public TodoConfiguration(){
	}
	
	public TodoConfiguration(@Nonnull final Message message){
		this(message, false);
	}
	
	public TodoConfiguration(@Nonnull final Message message, final boolean deleteOnDone){
		this(message.getChannel().getIdLong(), message.getIdLong(), deleteOnDone);
	}
	
	private TodoConfiguration(final long channelId, final long messageId, final boolean deleteOnDone){
		this.message = new MessageConfiguration(channelId, messageId);
		this.deleteOnDone = deleteOnDone;
	}
	
	@JsonCreator
	public void creator(@JsonProperty("channel") ChannelConfiguration channel, @JsonProperty("messageId") long messageId){
		this.message = new MessageConfiguration(channel.getChannelId(), messageId);
	}
	
	@Nonnull
	public MessageConfiguration getMessage(){
		return this.message;
	}
	
	public void setMessage(@Nonnull final Message message){
		this.message.setMessage(message);
	}
	
	public boolean isDeleteOnDone(){
		return deleteOnDone;
	}
	
	public void setDeleteOnDone(boolean deleteOnDone){
		this.deleteOnDone = deleteOnDone;
	}
}
