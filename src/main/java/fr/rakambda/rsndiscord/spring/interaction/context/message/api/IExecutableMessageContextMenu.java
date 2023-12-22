package fr.rakambda.rsndiscord.spring.interaction.context.message.api;

import fr.rakambda.rsndiscord.spring.interaction.slash.permission.IPermission;
import fr.rakambda.rsndiscord.spring.interaction.slash.permission.NoPermission;
import org.jetbrains.annotations.NotNull;

public interface IExecutableMessageContextMenu {
	@NotNull
	String getName();
	
	@NotNull
	default IPermission getPermission(){
		return new NoPermission();
	}
}
