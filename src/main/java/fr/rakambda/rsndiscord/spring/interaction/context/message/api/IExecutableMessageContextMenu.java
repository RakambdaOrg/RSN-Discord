package fr.rakambda.rsndiscord.spring.interaction.context.message.api;

import fr.rakambda.rsndiscord.spring.interaction.slash.permission.IPermission;
import fr.rakambda.rsndiscord.spring.interaction.slash.permission.NoPermission;
import org.jspecify.annotations.NonNull;

public interface IExecutableMessageContextMenu extends IMessageContextMenu {
	@NonNull
	String getName();
	
	@NonNull
	default IPermission getPermission(){
		return new NoPermission();
	}
}
