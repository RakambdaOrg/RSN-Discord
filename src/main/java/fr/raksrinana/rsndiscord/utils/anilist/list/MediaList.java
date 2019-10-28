package fr.raksrinana.rsndiscord.utils.anilist.list;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.anilist.AniListObject;
import fr.raksrinana.rsndiscord.utils.anilist.DatedObject;
import fr.raksrinana.rsndiscord.utils.anilist.FuzzyDate;
import fr.raksrinana.rsndiscord.utils.anilist.media.MangaMedia;
import fr.raksrinana.rsndiscord.utils.anilist.media.Media;
import fr.raksrinana.rsndiscord.utils.json.SQLTimestampDeserializer;
import net.dv8tion.jda.api.EmbedBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import static java.time.temporal.ChronoUnit.DAYS;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-12.
 *
 * @author Thomas Couchoud
 * @since 2018-10-12
 */
@SuppressWarnings({
		"ALL",
		"WeakerAccess"
})
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MediaList implements DatedObject{
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	private static final String QUERY = "mediaList(userId: $userID) {\n" + "id\n" + "private\n" + "progress\n" + "progressVolumes\n" + "priority\n" + "customLists\n" + "score(format: POINT_100)\n" + FuzzyDate.getQuery("completedAt") + "\n" + FuzzyDate.getQuery("startedAt") + "\n" + "status\n" + "updatedAt\n" + "createdAt\n" + Media.getQuery() + "\n}";
	@JsonProperty("id")
	private int id;
	@JsonProperty("status")
	private MediaListStatus status = MediaListStatus.UNKNOWN;
	@JsonProperty("media")
	private Media media;
	@JsonProperty("private")
	private boolean privateItem = false;
	@JsonProperty("priority")
	private Integer priority;
	@JsonProperty("progress")
	private Integer progress;
	@JsonProperty("progressVolumes")
	private Integer progressVolumes;
	@JsonProperty("createdAt")
	@JsonDeserialize(using = SQLTimestampDeserializer.class)
	private LocalDateTime createdAt;
	@JsonProperty("updatedAt")
	@JsonDeserialize(using = SQLTimestampDeserializer.class)
	private LocalDateTime updatedAt;
	@JsonProperty("startedAt")
	private FuzzyDate startedAt;
	@JsonProperty("completedAt")
	private FuzzyDate completedAt;
	@JsonProperty("customLists")
	private HashMap<String, Boolean> customLists;
	@JsonProperty("score")
	private Integer score;
	
	@Override
	public void fillEmbed(@Nonnull final EmbedBuilder builder){
		builder.setTimestamp(getDate());
		builder.setColor(getStatus().getColor());
		builder.setTitle("User list information", getMedia().getUrl().toString());
		builder.addField("List status", this.getStatus().toString(), true);
		if(Objects.nonNull(getScore())){
			builder.addField("Score", this.getScore() + "/100", true);
		}
		if(Objects.equals(isPrivateItem(), Boolean.TRUE)){
			builder.addField("Private", "Yes", true);
		}
		getStartedAt().asDate().ifPresent(date -> {
			builder.addField("Started at", date.format(DF), true);
		});
		getCompletedAt().asDate().ifPresent(date -> {
			builder.addField("Completed at", date.format(DF), true);
			getStartedAt().durationTo(date).ifPresent(duration -> {
				builder.addField("Time to complete", String.format("%d days", duration.get(DAYS)), true);
			});
		});
		builder.addField("Progress", getProgress() + "/" + Optional.ofNullable(getMedia().getItemCount()).map(Object::toString).orElse("?"), true);
		if(Objects.nonNull(getProgressVolumes()) && getMedia() instanceof MangaMedia){
			builder.addField("Volumes progress", getProgressVolumes() + "/" + Optional.ofNullable(((MangaMedia) getMedia()).getVolumes()).map(Object::toString).orElse("?"), true);
		}
		final var lists = Optional.ofNullable(this.customLists).orElse(new HashMap<>()).entrySet().stream().filter(k -> Objects.nonNull(k.getValue()) && k.getValue()).map(k -> k.getKey()).collect(Collectors.joining(", "));
		if(Objects.nonNull(lists) && !lists.isBlank()){
			builder.addField("In custom lists", lists, true);
		}
		builder.addBlankField(false);
		builder.addField("Media:", "", false);
		getMedia().fillEmbed(builder);
	}
	
	@Override
	@Nonnull
	public LocalDateTime getDate(){
		return this.updatedAt;
	}
	
	@Nonnull
	public MediaListStatus getStatus(){
		return status;
	}
	
	@Nonnull
	public Media getMedia(){
		return media;
	}
	
	@Nullable
	public Integer getScore(){
		return score;
	}
	
	public boolean isPrivateItem(){
		return privateItem;
	}
	
	@Nullable
	public FuzzyDate getStartedAt(){
		return startedAt;
	}
	
	@Nullable
	public FuzzyDate getCompletedAt(){
		return completedAt;
	}
	
	@Nullable
	public Integer getProgress(){
		return progress;
	}
	
	@Nullable
	private Integer getProgressVolumes(){
		return progressVolumes;
	}
	
	@Override
	public int getId(){
		return this.id;
	}
	
	@Override
	@Nonnull
	public URL getUrl(){
		return getMedia().getUrl();
	}
	
	@Override
	public boolean equals(@Nullable final Object obj){
		return obj instanceof MediaList && Objects.equals(((MediaList) obj).getId(), getId());
	}
	
	@Override
	public int hashCode(){
		return this.getId();
	}
	
	@Override
	public int compareTo(@Nonnull final AniListObject o){
		if(o instanceof DatedObject){
			return getDate().compareTo(((DatedObject) o).getDate());
		}
		return Integer.compare(getId(), o.getId());
	}
	
	@Nonnull
	public LocalDateTime getCreatedAt(){
		return createdAt;
	}
	
	@Nonnull
	public HashMap<String, Boolean> getCustomLists(){
		return customLists;
	}
	
	@Nullable
	public Integer getPriority(){
		return priority;
	}
	
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
	
	@Nonnull
	public static String getQuery(){
		return QUERY;
	}
}
