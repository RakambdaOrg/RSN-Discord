package fr.raksrinana.rsndiscord.utils.hermitcraft.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.json.URLDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.net.URL;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class Hermit{
	@JsonProperty("id")
	private int id;
	@JsonProperty("BeamName")
	private String beamName;
	@JsonProperty("ChannelName")
	private String channelName;
	@JsonProperty("DisplayName")
	private String displayName;
	@JsonProperty("ProfilePicture")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL profilePicture;
	@JsonProperty("TwitterName")
	private String twitterName;
	@JsonProperty("TwitchName")
	private String twitchName;
	@JsonProperty("WebsiteURL")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL websiteUrl;
	@JsonProperty("Active")
	private boolean active;
	@JsonProperty("Streaming")
	private boolean streaming;
	@JsonProperty("BeamStreaming")
	private boolean beamStreaming;
	@JsonProperty("YTStreaming")
	private boolean ytStreaming;
	@JsonProperty("ChannelID")
	private String channelId;
	@JsonProperty("UploadPlaylistID")
	private String uploadPlaylistId;
}
