package fr.raksrinana.rsndiscord.settings.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Message;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class TodoConfiguration{
	@JsonProperty("message")
	private MessageConfiguration message;
	@JsonProperty("deleteOnDone")
	private boolean deleteOnDone = false;
	
	public TodoConfiguration(@NonNull final Message message){
		this(message, false);
	}
	
	public TodoConfiguration(@NonNull final Message message, final boolean deleteOnDone){
		this(message.getChannel().getIdLong(), message.getIdLong(), deleteOnDone);
	}
	
	private TodoConfiguration(final long channelId, final long messageId, final boolean deleteOnDone){
		this.message = new MessageConfiguration(channelId, messageId);
		this.deleteOnDone = deleteOnDone;
	}
	
	public void setMessage(@NonNull final Message message){
		this.message.setMessage(message);
	}
	
	public void setDeleteOnDone(boolean deleteOnDone){
		this.deleteOnDone = deleteOnDone;
	}
}
