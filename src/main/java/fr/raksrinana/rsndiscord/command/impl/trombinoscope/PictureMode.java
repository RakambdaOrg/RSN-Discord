package fr.raksrinana.rsndiscord.command.impl.trombinoscope;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Objects;
import java.util.Optional;

public enum PictureMode{
	KEEP_ASPECT_RATIO,
	STRETCH;
	
	@NotNull
	public static Optional<PictureMode> fromString(@Nullable String value){
		for(var mode : PictureMode.values()){
			if(Objects.equals(value, mode.name())){
				return Optional.of(mode);
			}
		}
		return Optional.empty();
	}
}
