package fr.raksrinana.rsndiscord.utils.jda.wrappers.role;

import fr.raksrinana.rsndiscord.log.Log;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.managers.RoleManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.awt.Color;
import java.util.concurrent.CompletableFuture;

public class SetColorWrapper{
	private final Guild guild;
	private final Role role;
	private final Color color;
	private final RoleManager action;
	
	public SetColorWrapper(@NotNull Guild guild, @NotNull Role role, @Nullable Color color){
		this.guild = guild;
		this.role = role;
		this.color = color;
		this.action = role.getManager().setColor(color);
	}
	
	@NotNull
	public CompletableFuture<Void> submit(){
		return action.submit()
				.thenAccept(empty -> Log.getLogger(guild).info("Setting {} color to {}", role, color));
	}
}
