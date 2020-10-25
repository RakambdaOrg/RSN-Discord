package fr.raksrinana.rsndiscord.modules.trombinoscope.command;

import java.util.Objects;
import java.util.Optional;

public enum PictureMode{
	STRETCH,
	KEEP_ASPECT_RATIO;
	
	public static Optional<PictureMode> fromString(String value){
		for(var mode : PictureMode.values()){
			if(Objects.equals(value, mode.name())){
				return Optional.of(mode);
			}
		}
		return Optional.empty();
	}
}
