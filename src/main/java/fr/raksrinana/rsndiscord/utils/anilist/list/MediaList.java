package fr.raksrinana.rsndiscord.utils.anilist.list;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.anilist.AniListObject;
import fr.raksrinana.rsndiscord.utils.anilist.AnilistDatedObject;
import fr.raksrinana.rsndiscord.utils.anilist.FuzzyDate;
import fr.raksrinana.rsndiscord.utils.anilist.media.MangaMedia;
import fr.raksrinana.rsndiscord.utils.anilist.media.Media;
import fr.raksrinana.rsndiscord.utils.json.SQLTimestampDeserializer;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import java.net.URL;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.time.temporal.ChronoUnit.DAYS;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class MediaList implements AnilistDatedObject{
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	@Getter
	private static final String QUERY = "mediaList(userId: $userID) {\n" + "id\n" + "private\n" + "progress\n" + "progressVolumes\n" + "priority\n" + "customLists\n" + "score(format: POINT_100)\n" + FuzzyDate.getQuery("completedAt") + "\n" + FuzzyDate.getQuery("startedAt") + "\n" + "status\n" + "updatedAt\n" + "createdAt\n" + "repeat\n" + "notes\n" + Media.getQUERY() + "\n}";
	@JsonProperty("status")
	private MediaListStatus status = MediaListStatus.UNKNOWN;
	@JsonProperty("private")
	private boolean privateItem = false;
	@JsonProperty("startedAt")
	private FuzzyDate startedAt = new FuzzyDate();
	@JsonProperty("completedAt")
	private FuzzyDate completedAt = new FuzzyDate();
	@JsonProperty("customLists")
	private Map<String, Boolean> customLists = new HashMap<>();
	@JsonProperty("id")
	private int id;
	@JsonProperty("media")
	private Media media;
	@JsonProperty("priority")
	private Integer priority;
	@JsonProperty("progress")
	private Integer progress;
	@JsonProperty("progressVolumes")
	private Integer progressVolumes;
	@JsonProperty("createdAt")
	@JsonDeserialize(using = SQLTimestampDeserializer.class)
	private ZonedDateTime createdAt;
	@JsonProperty("updatedAt")
	@JsonDeserialize(using = SQLTimestampDeserializer.class)
	private ZonedDateTime updatedAt;
	@JsonProperty("score")
	private Integer score;
	@JsonProperty("repeat")
	private Integer repeat;
	@JsonProperty("notes")
	private String notes;
	
	@Override
	public void fillEmbed(@NonNull Locale locale, @NonNull final EmbedBuilder builder){
		builder.setTimestamp(getDate());
		builder.setColor(getStatus().getColor());
		builder.setTitle(translate(locale, "anilist.list-info"), getMedia().getUrl().toString());
		builder.addField(translate(locale, "anilist.list-status"), this.getStatus().toString(), true);
		if(Objects.nonNull(getScore())){
			builder.addField(translate(locale, "anilist.list-score"), this.getScore() + "/100", true);
		}
		if(Objects.equals(isPrivateItem(), Boolean.TRUE)){
			builder.addField(translate(locale, "anilist.list-private"), "Yes", true);
		}
		getStartedAt().asDate().ifPresent(date -> builder.addField(translate(locale, "anilist.list-started"), date.format(DF), true));
		getCompletedAt().asDate().ifPresent(date -> {
			builder.addField(translate(locale, "anilist.list-complete"), date.format(DF), true);
			getStartedAt().durationTo(date).ifPresent(duration -> builder.addField(translate(locale, "anilist.list-time"), String.format("%d days", duration.get(DAYS)), true));
		});
		builder.addField(translate(locale, "anilist.list-progress"), getProgress() + "/" + Optional.ofNullable(getMedia().getItemCount()).map(Object::toString).orElse("?"), true);
		if(Objects.nonNull(getProgressVolumes()) && getMedia() instanceof MangaMedia){
			builder.addField(translate(locale, "anilist.list-volumes"), getProgressVolumes() + "/" + Optional.ofNullable(((MangaMedia) getMedia()).getVolumes()).map(Object::toString).orElse("?"), true);
		}
		if(Objects.nonNull(getRepeat()) && getRepeat() > 0){
			builder.addField(translate(locale, "anilist.list-repeat"), Integer.toString(this.getRepeat()), true);
		}
		final var lists = Optional.ofNullable(this.customLists).orElse(new HashMap<>()).entrySet().stream().filter(k -> Objects.nonNull(k.getValue()) && k.getValue()).map(Map.Entry::getKey).collect(Collectors.joining(", "));
		if(!lists.isBlank()){
			builder.addField(translate(locale, "anilist.list-custom"), lists, true);
		}
		if(Objects.nonNull(getNotes()) && !getNotes().isBlank()){
			builder.addField(translate(locale, "anilist.list-notes"), getNotes(), false);
		}
		builder.addBlankField(false);
		getMedia().fillEmbed(locale, builder);
	}
	
	@Override
	@NonNull
	public ZonedDateTime getDate(){
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
		if(o instanceof AnilistDatedObject){
			return getDate().compareTo(((AnilistDatedObject) o).getDate());
		}
		return Integer.compare(getId(), o.getId());
	}
}
