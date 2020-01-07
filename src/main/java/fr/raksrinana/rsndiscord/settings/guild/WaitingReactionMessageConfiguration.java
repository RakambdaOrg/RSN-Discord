package fr.raksrinana.rsndiscord.settings.guild;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.settings.types.MessageConfiguration;
import fr.raksrinana.rsndiscord.utils.reaction.ReactionTag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Message;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class WaitingReactionMessageConfiguration{
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
}
