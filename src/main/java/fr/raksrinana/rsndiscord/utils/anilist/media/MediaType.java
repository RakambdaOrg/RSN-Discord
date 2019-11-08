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
public enum MediaType{
	ANIME(true, "Anime"), MANGA(false, "Manga");
	private final boolean shouldDisplay;
	private final String display;
	
	MediaType(final boolean shouldDisplay, @Nullable final String display){
		this.shouldDisplay = shouldDisplay;
		this.display = display;
	}
	
	@JsonCreator
	@Nonnull
	public static MediaType getFromString(@Nonnull final String value){
		return MediaType.valueOf(value);
	}
	
	public boolean shouldDisplay(){
		return this.shouldDisplay;
	}
	
	@Override
	public String toString(){
		return this.display;
	}
}
