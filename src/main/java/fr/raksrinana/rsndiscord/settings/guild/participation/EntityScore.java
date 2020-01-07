package fr.raksrinana.rsndiscord.settings.guild.participation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.settings.AtomicConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class EntityScore implements AtomicConfiguration{
	@JsonProperty("id")
	@Getter
	private long id;
	@JsonProperty("name")
	private String name;
	@JsonProperty("score")
	@Getter
	@Setter
	private long score;
	
	public EntityScore(final long id){
		this(id, null);
	}
	
	private EntityScore(final long id, final String name){
		this(id, name, 0);
	}
	
	public EntityScore(final long id, final String name, final long score){
		this.id = id;
		this.name = name;
		this.score = score;
	}
	
	public void increment(){
		this.score++;
	}
	
	@Override
	public int hashCode(){
		return new HashCodeBuilder(17, 37).append(this.getId()).toHashCode();
	}
	
	@Override
	public boolean equals(final Object o){
		if(this == o){
			return true;
		}
		if(!(o instanceof EntityScore)){
			return false;
		}
		final var that = (EntityScore) o;
		return new EqualsBuilder().append(this.getId(), that.getId()).isEquals();
	}
	
	@Override
	public boolean shouldBeRemoved(){
		return false;
	}
	
	@NonNull
	public Optional<String> getName(){
		return Optional.ofNullable(this.name);
	}
}
