package fr.mrcraftcod.gunterdiscord.utils.anilist.notifications.airing;

import fr.mrcraftcod.gunterdiscord.utils.anilist.AniListDatedObject;
import fr.mrcraftcod.gunterdiscord.utils.anilist.JSONFiller;
import fr.mrcraftcod.gunterdiscord.utils.anilist.media.AniListMedia;
import fr.mrcraftcod.gunterdiscord.utils.anilist.notifications.AniListNotificationType;
import net.dv8tion.jda.core.EmbedBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.json.JSONObject;
import java.awt.*;
import java.util.Date;
import java.util.Objects;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-11.
 *
 * @author Thomas Couchoud
 * @since 2018-10-11
 */
public class AniListAiringNotification implements JSONFiller, AniListDatedObject{
	private static final String QUERY = "AiringNotification {\n" + "id\n" + "type\n" + "episode\n" + "createdAt\n" + AniListMedia.getQuery() + "}\n";
	private final AniListNotificationType type;
	private int episode;
	private Date createdAt;
	private AniListMedia media;
	private int id;
	
	public AniListAiringNotification(){
		this.type = AniListNotificationType.AIRING;
	}
	
	public static AniListAiringNotification buildFromJSON(final JSONObject json) throws Exception{
		final var notification = new AniListAiringNotification();
		notification.fromJSON(json);
		return notification;
	}
	
	@Override
	public void fromJSON(final JSONObject json) throws Exception{
		this.id = json.getInt("id");
		this.episode = json.getInt("episode");
		this.createdAt = new Date(json.getInt("createdAt") * 1000L);
		this.media = AniListMedia.buildFromJSON(json.getJSONObject("media"));
	}
	
	@Override
	public boolean equals(final Object obj){
		if(!(obj instanceof AniListAiringNotification)){
			return false;
		}
		final var notification = (AniListAiringNotification) obj;
		return Objects.equals(notification.getEpisode(), getEpisode()) && Objects.equals(notification.getMedia(), getMedia());
	}
	
	@Override
	public String toString(){
		return new ToStringBuilder(this).append("episode", episode).append("createdAt", createdAt).append("type", type).append("media", media).append("id", id).toString();
	}
	
	@Override
	public int getId(){
		return this.id;
	}
	
	@Override
	public void fillEmbed(final EmbedBuilder builder){
		builder.setTimestamp(getDate().toInstant());
		builder.setColor(Color.GREEN);
		builder.setDescription("New release");
		builder.addField("Episode", "" + getEpisode(), true);
		getMedia().fillEmbed(builder);
	}
	
	@Override
	public Date getDate(){
		return createdAt;
	}
	
	public int getEpisode(){
		return episode;
	}
	
	private AniListMedia getMedia(){
		return this.media;
	}
	
	@Override
	public String getUrl(){
		return null;
	}
	
	public static String getQuery(){
		return QUERY;
	}
	
	public AniListNotificationType getType(){
		return type;
	}
}
