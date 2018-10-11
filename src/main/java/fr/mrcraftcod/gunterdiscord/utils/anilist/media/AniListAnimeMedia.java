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
public class AniListAnimeMedia extends AniListMedia{
	private Integer episodes;
	
	public AniListAnimeMedia(){
		super(AniListMediaType.ANIME);
	}
	
	@Override
	public void fillEmbed(final EmbedBuilder builder){
		super.fillEmbed(builder);
		Optional.ofNullable(getEpisodes()).map(Object::toString).ifPresent(val -> builder.addField("Episodes", val, true));
		Optional.ofNullable(getSeason()).map(Enum::name).ifPresent(val -> builder.addField("Season", val, true));
	}
	
	@Override
	public void fromJSON(final JSONObject json) throws Exception{
		super.fromJSON(json);
		this.episodes = Utilities.getJSONMaybe(json, Integer.class, "episodes");
	}
	
	@Override
	public String getProgressType(final boolean contains){
		return "watched episode";
	}
	
	@Override
	public String toString(){
		return new ToStringBuilder(this).append("episodes", episodes).toString();
	}
	
	public Integer getEpisodes(){
		return episodes;
	}
}
