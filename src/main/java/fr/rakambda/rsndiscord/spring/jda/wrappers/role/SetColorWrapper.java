package fr.rakambda.rsndiscord.spring.jda.wrappers.role;

import fr.rakambda.rsndiscord.spring.jda.ActionWrapper;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.managers.RoleManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.awt.Color;

@Slf4j
public class SetColorWrapper extends ActionWrapper<Void, RoleManager>{
	private final Role role;
	private final Color color;
	
	public SetColorWrapper(@NotNull Role role, @Nullable Color color){
		super(role.getManager().setColor(color));
		this.role = role;
		this.color = color;
	}
	
	@Override
	protected void logSuccess(Void value){
		log.info("Setting {} color to {}", role, color);
	}
	
	@Override
	protected void logException(Throwable throwable){
		log.error("Failed to set color {} to role {}", color, role, throwable);
	}
}
