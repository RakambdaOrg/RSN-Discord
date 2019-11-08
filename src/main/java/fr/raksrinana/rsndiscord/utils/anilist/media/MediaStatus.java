package fr.raksrinana.rsndiscord.utils.anilist.media;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-10.
 *
 * @author Thomas Couchoud
 * @since 2018-10-10
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public enum MediaStatus{
	FINISHED("Finished"), RELEASING("Releasing"), NOT_YET_RELEASED("Not yet released"), CANCELLED("Cancelled");
	private final String display;
	
	MediaStatus(@Nullable final String display){
		this.display = display;
	}
	
	@JsonCreator
	@Nonnull
	public static MediaStatus getFromString(@Nonnull final String value){
		return MediaStatus.valueOf(value);
	}
	
	@Override
	public String toString(){
		return this.display;
	}
}
