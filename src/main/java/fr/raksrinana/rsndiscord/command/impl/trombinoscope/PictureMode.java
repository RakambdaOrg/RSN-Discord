package fr.raksrinana.rsndiscord.command.impl.trombinoscope;

import java.util.Objects;
import java.util.Optional;

public enum PictureMode{
	KEEP_ASPECT_RATIO,
	STRETCH;
	
	public static Optional<PictureMode> fromString(String value){
		for(var mode : PictureMode.values()){
			if(Objects.equals(value, mode.name())){
				return Optional.of(mode);
			}
		}
		return Optional.empty();
	}
}
