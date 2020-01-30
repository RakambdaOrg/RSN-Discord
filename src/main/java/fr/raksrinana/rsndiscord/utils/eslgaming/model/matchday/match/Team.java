package fr.raksrinana.rsndiscord.utils.eslgaming.model.matchday.match;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.json.IntegerBooleanDeserializer;
import fr.raksrinana.rsndiscord.utils.json.SQLTimestampDeserializer;
import fr.raksrinana.rsndiscord.utils.json.URLDeserializer;
import fr.raksrinana.rsndiscord.utils.json.UnknownDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class Team{
	@JsonProperty("uid")
	private int uid;
	@JsonProperty("pid")
	private int pid;
	@JsonProperty("tstamp")
	@JsonDeserialize(using = SQLTimestampDeserializer.class)
	private LocalDateTime timestamp;
	@JsonProperty("crdate")
	@JsonDeserialize(using = SQLTimestampDeserializer.class)
	private LocalDateTime creationDate;
	@JsonProperty("cruser_id")
	private long creationUserId;
	@JsonProperty("sorting")
	private int sorting;
	@JsonProperty("deleted")
	@JsonDeserialize(using = IntegerBooleanDeserializer.class)
	private boolean deleted;
	@JsonProperty("hidden")
	@JsonDeserialize(using = IntegerBooleanDeserializer.class)
	private boolean hidden;
	@JsonProperty("name")
	private String name;
	@JsonProperty("short")
	private String shortName;
	@JsonProperty("url")
	private String url;
	@JsonProperty("parent_name")
	private String parentName;
	@JsonProperty("founded")
	@JsonDeserialize(using = IntegerBooleanDeserializer.class)
	private boolean founded;
	@JsonProperty("joined")
	@JsonDeserialize(using = IntegerBooleanDeserializer.class)
	private boolean joined;
	@JsonProperty("eslid")
	private int eslId;
	@JsonProperty("esea_id")
	private int eseaId;
	@JsonProperty("country")
	private String country;
	@JsonProperty("flag")
	private String flag;
	@JsonProperty("homepage")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL homepage;
	@JsonProperty("twitter")
	private String twitter;
	@JsonProperty("facebook")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL facebook;
	@JsonProperty("intro")
	private String intro;
	@JsonProperty("history")
	@JsonDeserialize(using = UnknownDeserializer.class)
	private Object history;
	@JsonProperty("achievements")
	@JsonDeserialize(using = UnknownDeserializer.class)
	private Object achievements;
	@JsonProperty("logo_small")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL logoSmall;
	@JsonProperty("logo_medium")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL logoMedium;
	@JsonProperty("logoLarge")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL logoLarge;
	@JsonProperty("logo_transparent")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL logoTransparent;
	@JsonProperty("logo_transparent_blackbg")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL logoTransparentBlackBg;
	@JsonProperty("logo_transparent_whitebg")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL logoTransparentWhiteBg;
	@JsonProperty("memberphoto")
	private int memberPhoto;
	@JsonProperty("category")
	private String category;
	@JsonProperty("game")
	private String game;
	@JsonDeserialize(using = IntegerBooleanDeserializer.class)
	@JsonProperty("hideplayerphoto")
	private boolean hidePlayerPhoto;
	@JsonProperty("disqualified")
	@JsonDeserialize(using = IntegerBooleanDeserializer.class)
	private boolean disqualified;
	@JsonProperty("disqualified_text")
	private String disqualifiedText;
	@JsonProperty("dnf")
	private String dnf;
	@JsonProperty("links")
	private String links;
	@JsonProperty("prodbkey")
	private String prodBKey;
	@JsonProperty("prodbkey_team")
	private String prodBKeyTeam;
	@JsonProperty("prodbkey_squad")
	private String prodBKeySquad;
	@JsonProperty("updateviaprodb")
	@JsonDeserialize(using = IntegerBooleanDeserializer.class)
	private boolean updateViaProdB;
	@JsonProperty("vk")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL vk;
	@JsonProperty("youtube_channel")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL youtubeChannel;
	@JsonProperty("twitch_channel")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL twitchChannel;
	@JsonProperty("shoplink")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL shopLink;
	@JsonProperty("sponsor1_url")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL sponsor1Url;
	@JsonProperty("sponsor2_url")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL sponsor2Url;
	@JsonProperty("sponsor3_url")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL sponsor3Url;
	@JsonProperty("sponsor4_url")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL sponsor4Url;
	@JsonProperty("sponsor5_url")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL sponsor5Url;
	@JsonProperty("sponsor6_url")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL sponsor6Url;
	@JsonProperty("sponsor1_logo")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL sponsor1Logo;
	@JsonProperty("sponsor2_logo")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL sponsor2Logo;
	@JsonProperty("sponsor3_logo")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL sponsor3Logo;
	@JsonProperty("sponsor4_logo")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL sponsor4Logo;
	@JsonProperty("sponsor5_logo")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL sponsor5Logo;
	@JsonProperty("sponsor6_logo")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL sponsor6Logo;
	@JsonProperty("stats")
	private String stats;
	@JsonProperty("needsprodbupdate")
	@JsonDeserialize(using = IntegerBooleanDeserializer.class)
	private boolean needsProdBUpdate;
	
	@Override
	public int hashCode(){
		return Objects.hash(getUid());
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(!(o instanceof Team)){
			return false;
		}
		Team team = (Team) o;
		return getUid() == team.getUid();
	}
	
	@Override
	public String toString(){
		return getName();
	}
}
