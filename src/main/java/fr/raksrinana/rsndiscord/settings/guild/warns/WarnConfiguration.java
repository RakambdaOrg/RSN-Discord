package fr.raksrinana.rsndiscord.settings.guild.warns;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.settings.AtomicConfiguration;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Role;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class WarnConfiguration implements AtomicConfiguration{
	@JsonProperty("role")
	private RoleConfiguration role;
	@JsonProperty("delay")
	@Setter
	private long delay = 1L;
	
	public WarnConfiguration(@NonNull final Role role, final long delay){
		this.role = new RoleConfiguration(role);
		this.delay = delay;
	}
	
	@Override
	public int hashCode(){
		return new HashCodeBuilder(17, 37).append(this.getRole()).append(this.getDelay()).toHashCode();
	}
	
	@Override
	public boolean equals(final Object o){
		if(this == o){
			return true;
		}
		if(!(o instanceof WarnConfiguration)){
			return false;
		}
		final var that = (WarnConfiguration) o;
		return new EqualsBuilder().append(this.getDelay(), that.getDelay()).append(this.getRole(), that.getRole()).isEquals();
	}
	
	@Override
	public String toString(){
		return this.getRole().toString() + " for " + this.getDelay() + " seconds";
	}
	
	@Override
	public boolean shouldBeRemoved(){
		return role.shouldBeRemoved();
	}
}
