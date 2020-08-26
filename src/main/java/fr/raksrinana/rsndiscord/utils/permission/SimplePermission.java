package fr.raksrinana.rsndiscord.utils.permission;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SimplePermission implements Permission{
	private String id;
	private boolean allowedByDefault;
}
