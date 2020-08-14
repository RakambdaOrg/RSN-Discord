package fr.raksrinana.rsndiscord.settings.guild;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.settings.AtomicConfiguration;
import fr.raksrinana.rsndiscord.settings.types.MessageConfiguration;
import fr.raksrinana.rsndiscord.utils.reaction.ReactionTag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Message;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class WaitingReactionMessageConfiguration implements AtomicConfiguration{
	@JsonProperty("message")
	private MessageConfiguration message;
	@JsonProperty("tag")
	private ReactionTag tag = ReactionTag.NONE;
	@JsonProperty("data")
	private Map<String, String> data = new HashMap<>();
	
	public WaitingReactionMessageConfiguration(@NonNull Message message, @NonNull ReactionTag tag){
		this(message, tag, null);
	}
	
	public WaitingReactionMessageConfiguration(@NonNull Message message, @NonNull ReactionTag tag, Map<String, String> data){
		this(new MessageConfiguration(message), tag, data);
	}
	
	public WaitingReactionMessageConfiguration(@NonNull MessageConfiguration message, @NonNull ReactionTag tag, Map<String, String> data){
		this.message = message;
		this.tag = tag;
		this.data = data;
	}
	
	@Override
	public int hashCode(){
		return Objects.hash(getMessage(), getTag());
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(o == null || getClass() != o.getClass()){
			return false;
		}
		WaitingReactionMessageConfiguration that = (WaitingReactionMessageConfiguration) o;
		return getMessage().equals(that.getMessage()) && getTag() == that.getTag();
	}
	
	@Override
	public boolean shouldBeRemoved(){
		return getMessage().shouldBeRemoved();
	}
	
	@Override
	public String toString(){
		return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
				.append("message", message)
				.append("tag", tag)
				.append("data", data)
				.toString();
	}
}
