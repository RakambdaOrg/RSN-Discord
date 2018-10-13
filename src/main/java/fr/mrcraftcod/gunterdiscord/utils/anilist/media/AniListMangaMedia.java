package fr.mrcraftcod.gunterdiscord.utils.anilist.media;

import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.core.EmbedBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.json.JSONObject;
import java.util.Optional;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-11.
 *
 * @author Thomas Couchoud
 * @since 2018-10-11
 */
public class AniListMangaMedia extends AniListMedia{
	private Integer chapters;
	
	public AniListMangaMedia(){
		super(AniListMediaType.MANGA);
	}
	
	@Override
	public void fillEmbed(final EmbedBuilder builder){
		super.fillEmbed(builder);
		Optional.ofNullable(getChapters()).map(Object::toString).ifPresent(val -> builder.addField("Chapters", val, true));
	}
	
	@Override
	public void fromJSON(final JSONObject json) throws Exception{
		super.fromJSON(json);
		this.chapters = Utilities.getJSONMaybe(json, Integer.class, "chapters");
	}
	
	@Override
	public String getProgressType(final boolean contains){
		return "read chapter";
	}
	
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
	
	public Integer getChapters(){
		return chapters;
	}
}
