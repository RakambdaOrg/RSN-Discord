package fr.raksrinana.rsndiscord.utils.overwatch.year2020.content.utils.asset;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.json.ISO8601ZonedDateTimeDeserializer;
import fr.raksrinana.rsndiscord.utils.json.URLDeserializer;
import fr.raksrinana.rsndiscord.utils.json.UnknownDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class Asset{
	@JsonProperty("uid")
	private String uid;
	@JsonProperty("createdAt")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	private ZonedDateTime createdAt;
	@JsonProperty("updatedAt")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	private ZonedDateTime updatedAt;
	@JsonProperty("createdBy")
	private String createdBy;
	@JsonProperty("updatedBy")
	private String updatedBy;
	@JsonProperty("contentType")
	private String contentType;
	@JsonProperty("filesize")
	private long fileSize;
	@JsonProperty("tags")
	@JsonDeserialize(contentUsing = UnknownDeserializer.class)
	private Set<Object> tags;
	@JsonProperty("filename")
	private String filename;
	@JsonProperty("url")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL url;
	@JsonProperty("isDir")
	private boolean isDir;
	@JsonProperty("Version")
	private int version;
	@JsonProperty("title")
	private String title;
	@JsonProperty("publishDetails")
	private PublishDetails publishDetails;
	
	@Override
	public int hashCode(){
		return Objects.hash(getUid());
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(!(o instanceof Asset)){
			return false;
		}
		Asset asset = (Asset) o;
		return Objects.equals(getUid(), asset.getUid());
	}
	
	@Override
	public String toString(){
		return getFilename();
	}
}
