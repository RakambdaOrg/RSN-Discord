package fr.raksrinana.rsndiscord.utils.anilist.activity.list;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-10.
 *
 * @author Thomas Couchoud
 * @since 2018-10-10
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public enum ActivityType{
	ANIME_LIST(AnimeListActivity.class), MANGA_LIST(MangaListActivity.class);
	private final Class<? extends ListActivity> klass;
	
	ActivityType(@Nonnull final Class<? extends ListActivity> klass){
		this.klass = klass;
	}
	
	@JsonCreator
	@Nonnull
	public static ActivityType getFromString(@Nonnull final String value){
		return ActivityType.valueOf(value);
	}
	
	@Nonnull
	public ListActivity getInstance() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException{
		return this.getKlass().getConstructor().newInstance();
	}
	
	@Nonnull
	private Class<? extends ListActivity> getKlass(){
		return this.klass;
	}
}
