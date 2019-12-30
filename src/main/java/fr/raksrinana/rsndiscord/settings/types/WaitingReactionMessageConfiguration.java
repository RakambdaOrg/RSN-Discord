package fr.raksrinana.rsndiscord.settings.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.utils.reaction.ReactionTag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Message;
import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class WaitingReactionMessageConfiguration{
	@JsonProperty("message")
	private MessageConfiguration message;
	@JsonProperty("tag")
	private ReactionTag tag;
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
}
