package fr.mrcraftcod.gunterdiscord.utils.anilist.list;

import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import fr.mrcraftcod.gunterdiscord.utils.anilist.AniListDatedObject;
import fr.mrcraftcod.gunterdiscord.utils.anilist.AniListObject;
import fr.mrcraftcod.gunterdiscord.utils.anilist.FuzzyDate;
import fr.mrcraftcod.gunterdiscord.utils.anilist.media.AniListMedia;
import net.dv8tion.jda.core.EmbedBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-12.
 *
 * @author Thomas Couchoud
 * @since 2018-10-12
 */
public class AniListMediaList implements AniListDatedObject{
	private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
	private static final String QUERY = "mediaList(userId: $userID) {\n" + "id\n" + "private\n" + "progress\n" + "priority\n" + "customLists\n" + "score(format: POINT_100)\n" + "completedAt{year month day}" + "startedAt{year month day}" + "status\n" + "updatedAt\n" + "createdAt\n" + AniListMedia.getQuery() + "}";
	private int id;
	private AniListMediaListStatus status;
	private AniListMedia media;
	private boolean privateItem;
	private Integer priority;
	private Integer progress;
	private Date createdAt;
	private Date updatedAt;
	private FuzzyDate startedAt;
	private FuzzyDate completedAt;
	private HashMap<String, Boolean> customLists;
	private Integer score;
	
	public static AniListMediaList buildFromJSON(final JSONObject json) throws Exception{
		final var mediaList = new AniListMediaList();
		mediaList.fromJSON(json);
		return mediaList;
	}
	
	@Override
	public void fromJSON(final JSONObject json) throws Exception{
		this.id = json.getInt("id");
		this.privateItem = json.getBoolean("private");
		this.priority = Utilities.getJSONMaybe(json, Integer.class, "priority");
		this.progress = Utilities.getJSONMaybe(json, Integer.class, "progress");
		this.score = Utilities.getJSONMaybe(json, Integer.class, "score");
		this.status = AniListMediaListStatus.valueOf(json.getString("status"));
		this.media = AniListMedia.buildFromJSON(json.getJSONObject("media"));
		this.createdAt = new Date(json.optInt("createdAt") * 1000L);
		this.updatedAt = new Date(json.optInt("updatedAt") * 1000L);
		this.startedAt = FuzzyDate.buildFromJSON(json, "startedAt");
		this.completedAt = FuzzyDate.buildFromJSON(json, "completedAt");
		this.customLists = new HashMap<>();
		final var customListsJson = json.optJSONObject("customLists");
		if(Objects.nonNull(customListsJson)){
			for(final var list : customListsJson.keySet()){
				customLists.put(list, customListsJson.getBoolean(list));
			}
		}
	}
	
	@Override
	public void fillEmbed(final EmbedBuilder builder){
		builder.setTimestamp(getDate().toInstant());
		builder.setColor(getStatus().getColor());
		builder.setDescription("List changed");
		builder.addField("List status", this.getStatus().toString(), true);
		if(Objects.nonNull(getScore())){
			builder.addField("Score", this.getScore().toString(), true);
		}
		if(Objects.equals(isPrivateItem(), Boolean.TRUE)){
			builder.addField("Private", "Yes", true);
		}
		if(Objects.nonNull(getStartedAt())){
			builder.addField("Started at", SIMPLE_DATE_FORMAT.format(getStartedAt().asDate()), true);
		}
		if(Objects.nonNull(getCompletedAt())){
			builder.addField("Completed at", SIMPLE_DATE_FORMAT.format(getCompletedAt().asDate()), true);
		}
		builder.addField("Progress", getProgress() + "/" + Optional.ofNullable(getMedia().getItemCount()).map(Object::toString).orElse("?"), true);
		
		final var lists = this.customLists.keySet().stream().filter(k -> customLists.get(k)).collect(Collectors.joining(", "));
		if(Objects.nonNull(lists) && !lists.isBlank()){
			builder.addField("In custom lists", lists, true);
		}
		
		builder.addBlankField(false);
		builder.addField("Media:", "", false);
		getMedia().fillEmbed(builder);
	}
	
	@Override
	public Date getDate(){
		return this.updatedAt;
	}
	
	public Integer getScore(){
		return score;
	}
	
	public AniListMediaListStatus getStatus(){
		return status;
	}
	
	public boolean isPrivateItem(){
		return privateItem;
	}
	
	public FuzzyDate getStartedAt(){
		return startedAt;
	}
	
	public FuzzyDate getCompletedAt(){
		return completedAt;
	}
	
	public Integer getProgress(){
		return progress;
	}
	
	public AniListMedia getMedia(){
		return media;
	}
	
	@Override
	public int getId(){
		return this.id;
	}
	
	@Override
	public String getUrl(){
		return getMedia().getUrl();
	}
	
	@Override
	public int compareTo(@NotNull final AniListObject o){
		if(o instanceof AniListDatedObject){
			return getDate().compareTo(((AniListDatedObject) o).getDate());
		}
		return Integer.compare(getId(), o.getId());
	}
	
	@Override
	public int hashCode(){
		return this.getId();
	}
	
	@Override
	public boolean equals(final Object obj){
		return obj instanceof AniListMediaList && Objects.equals(((AniListMediaList) obj).getId(), getId());
	}
	
	public Date getCreatedAt(){
		return createdAt;
	}
	
	public Integer getPriority(){
		return priority;
	}
	
	public static String getQuery(){
		return QUERY;
	}
	
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
}
