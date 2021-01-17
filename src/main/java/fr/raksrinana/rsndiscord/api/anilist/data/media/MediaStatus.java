package fr.raksrinana.rsndiscord.api.anilist.data.media;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.jetbrains.annotations.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public enum MediaStatus{
	CANCELLED("Cancelled"),
	FINISHED("Finished"),
	NOT_YET_RELEASED("Not yet released"),
	RELEASING("Releasing");
	
	private final String value;
	
	MediaStatus(String value){
		this.value = value;
	}
	
	@JsonCreator
	@NotNull
	public static MediaStatus getFromName(@NotNull String value){
		return MediaStatus.valueOf(value);
	}
	
	@Override
	public String toString(){
		return value;
	}
}
