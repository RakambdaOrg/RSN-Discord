package fr.rakambda.rsndiscord.spring.api.hermitcraft.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.rakambda.rsndiscord.spring.json.converter.URLDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.net.URL;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Hermit{
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
	
	@Override
	public String toString(){
		return getDisplayName();
	}
	
	public boolean isLive(){
		return isStreaming() || isYtStreaming() || isBeamStreaming();
	}
}
