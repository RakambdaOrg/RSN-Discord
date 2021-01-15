package fr.raksrinana.rsndiscord.api.anilist.data.media;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.NonNull;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public enum MediaStatus{
	CANCELLED("Cancelled"),
	FINISHED("Finished"),
	NOT_YET_RELEASED("Not yet released"),
	RELEASING("Releasing");
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
