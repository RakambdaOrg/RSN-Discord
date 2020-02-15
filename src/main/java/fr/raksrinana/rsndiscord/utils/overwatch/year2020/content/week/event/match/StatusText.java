package fr.raksrinana.rsndiscord.utils.overwatch.year2020.content.week.event.match;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.json.ISO8601DateTimeDeserializer;
import fr.raksrinana.rsndiscord.utils.json.UnknownDeserializer;
import fr.raksrinana.rsndiscord.utils.overwatch.year2020.content.utils.asset.PublishDetails;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class StatusText{
	@JsonProperty("locale")
	private String locale;
	@JsonProperty("ACL")
	@JsonDeserialize(using = UnknownDeserializer.class)
	private Object acl;
	@JsonProperty("ContentTypeUid")
	private String contentTypeUid;
	@JsonProperty("InProgress")
	private boolean inProgress;
	@JsonProperty("Version")
	private int version;
	@JsonProperty("createdAt")
	@JsonDeserialize(using = ISO8601DateTimeDeserializer.class)
	private LocalDateTime createdAt;
	@JsonProperty("createdBy")
	private String createdBy;
	@JsonProperty("tags")
	@JsonDeserialize(using = UnknownDeserializer.class)
	private Set<Object> tags;
	@JsonProperty("title")
	private String title;
	@JsonProperty("uid")
	private String uid;
	@JsonProperty("updatedAt")
	@JsonDeserialize(using = ISO8601DateTimeDeserializer.class)
	private LocalDateTime updatedAt;
	@JsonProperty("updatedBy")
	private String updatedBy;
	@JsonProperty("value")
	private String value;
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
		if(!(o instanceof StatusText)){
			return false;
		}
		StatusText that = (StatusText) o;
		return Objects.equals(getUid(), that.getUid());
	}
}
