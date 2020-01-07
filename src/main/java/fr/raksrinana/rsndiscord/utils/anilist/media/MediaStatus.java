package fr.raksrinana.rsndiscord.utils.anilist.media;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.NonNull;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public enum MediaStatus{
	FINISHED("Finished"), RELEASING("Releasing"), NOT_YET_RELEASED("Not yet released"), CANCELLED("Cancelled");
	private final String display;
	
	MediaStatus(final String display){
		this.display = display;
	}
	
	@JsonCreator
	@NonNull
	public static MediaStatus getFromString(@NonNull final String value){
		return MediaStatus.valueOf(value);
	}
	
	@Override
	public String toString(){
		return this.display;
	}
}
