package fr.raksrinana.rsndiscord.utils.jda.wrappers.role;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.managers.RoleManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.awt.Color;
import java.util.concurrent.CompletableFuture;

@Log4j2
public class SetColorWrapper{
	private final Role role;
	private final Color color;
	private final RoleManager action;
	
	public SetColorWrapper(@NotNull Role role, @Nullable Color color){
		this.role = role;
		this.color = color;
		action = role.getManager().setColor(color);
	}
	
	@NotNull
	public CompletableFuture<Void> submit(){
		return action.submit().thenAccept(empty -> log.info("Setting {} color to {}", role, color));
	}
}
