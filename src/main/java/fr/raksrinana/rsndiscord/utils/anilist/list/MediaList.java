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
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import static java.time.temporal.ChronoUnit.DAYS;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class MediaList implements DatedObject{
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	@Getter
	private static final String QUERY = "mediaList(userId: $userID) {\n" + "id\n" + "private\n" + "progress\n" + "progressVolumes\n" + "priority\n" + "customLists\n" + "score(format: POINT_100)\n" + FuzzyDate.getQuery("completedAt") + "\n" + FuzzyDate.getQuery("startedAt") + "\n" + "status\n" + "updatedAt\n" + "createdAt\n" + "repeat\n" + "notes\n" + Media.getQUERY() + "\n}";
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
	private Map<String, Boolean> customLists = new HashMap<>();
	@JsonProperty("score")
	private Integer score;
	@JsonProperty("repeat")
	private Integer repeat;
	@JsonProperty("notes")
	private String notes;
	
	@Override
	public void fillEmbed(@NonNull final EmbedBuilder builder){
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
		getStartedAt().asDate().ifPresent(date -> builder.addField("Started at", date.format(DF), true));
		getCompletedAt().asDate().ifPresent(date -> {
			builder.addField("Completed at", date.format(DF), true);
			getStartedAt().durationTo(date).ifPresent(duration -> builder.addField("Time to complete", String.format("%d days", duration.get(DAYS)), true));
		});
		builder.addField("Progress", getProgress() + "/" + Optional.ofNullable(getMedia().getItemCount()).map(Object::toString).orElse("?"), true);
		if(Objects.nonNull(getProgressVolumes()) && getMedia() instanceof MangaMedia){
			builder.addField("Volumes progress", getProgressVolumes() + "/" + Optional.ofNullable(((MangaMedia) getMedia()).getVolumes()).map(Object::toString).orElse("?"), true);
		}
		if(Objects.nonNull(getRepeat()) && getRepeat() > 0){
			builder.addField("Repeat count", Integer.toString(this.getRepeat()), true);
		}
		final var lists = Optional.ofNullable(this.customLists).orElse(new HashMap<>()).entrySet().stream().filter(k -> Objects.nonNull(k.getValue()) && k.getValue()).map(Map.Entry::getKey).collect(Collectors.joining(", "));
		if(!lists.isBlank()){
			builder.addField("In custom lists", lists, true);
		}
		if(Objects.nonNull(getNotes()) && !getNotes().isBlank()){
			builder.addField("Notes", getNotes(), false);
		}
		builder.addBlankField(false);
		builder.addField("Media:", "", false);
		getMedia().fillEmbed(builder);
	}
	
	@Override
	@NonNull
	public LocalDateTime getDate(){
		return this.getUpdatedAt();
	}
	
	@Override
	@NonNull
	public URL getUrl(){
		return getMedia().getUrl();
	}
	
	@Override
	public int hashCode(){
		return this.getId();
	}
	
	@Override
	public boolean equals(final Object obj){
		return obj instanceof MediaList && Objects.equals(((MediaList) obj).getId(), getId());
	}
	
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
	
	@Override
	public int compareTo(@NonNull final AniListObject o){
		if(o instanceof DatedObject){
			return getDate().compareTo(((DatedObject) o).getDate());
		}
		return Integer.compare(getId(), o.getId());
	}
}
