package fr.mrcraftcod.gunterdiscord.newSettings.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.mrcraftcod.gunterdiscord.Main;
import net.dv8tion.jda.api.entities.Role;
import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by mrcraftcod (MrCraftCod - zerderr@gmail.com) on 2019-06-23.
 *
 * @author Thomas Couchoud
 * @since 2019-06-23
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoleConfiguration{
	@JsonProperty("roleId")
	private long roleId;
	
	public RoleConfiguration(){
	}
	
	public RoleConfiguration(final Role role){
		this(role.getIdLong());
	}
	
	public RoleConfiguration(final long roleId){
		this.roleId = roleId;
	}
	
	public void setRole(@Nonnull Role role){
		this.setRole(role.getIdLong());
	}
	
	private void setRole(long roleId){
		this.roleId = roleId;
	}
	
	@Nonnull
	public Optional<Role> getRole(){
		return Optional.ofNullable(Main.getJDA().getRoleById(this.getRoleId()));
	}
	
	public long getRoleId(){
		return this.roleId;
	}
	
	@Override
	public String toString(){
		return this.getRole().map(Role::getAsMention).orElse("");
	}
	
	@Override
	public boolean equals(Object obj){
		return obj instanceof RoleConfiguration && Objects.equals(this.getRoleId(), ((RoleConfiguration) obj).getRoleId());
	}
}
