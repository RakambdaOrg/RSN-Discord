package fr.raksrinana.rsndiscord.utils.jda.wrappers.role;

import fr.raksrinana.rsndiscord.utils.jda.ActionWrapper;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.managers.RoleManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.awt.Color;

@Log4j2
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
}
