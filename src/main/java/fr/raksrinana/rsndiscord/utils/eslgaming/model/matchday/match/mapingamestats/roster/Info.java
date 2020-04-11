package fr.raksrinana.rsndiscord.utils.eslgaming.model.matchday.match.mapingamestats.roster;

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
import java.time.ZonedDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class Info{
	@JsonProperty("uid")
	private int uid;
	@JsonProperty("pid")
	private int pid;
	@JsonProperty("tstamp")
	@JsonDeserialize(using = SQLTimestampDeserializer.class)
	private ZonedDateTime timestamp;
	@JsonProperty("crdate")
	@JsonDeserialize(using = SQLTimestampDeserializer.class)
	private ZonedDateTime createdDate;
	@JsonProperty("cruser_id")
	private int creationUserId;
	@JsonProperty("sorting")
	private int sorting;
	@JsonProperty("deleted")
	@JsonDeserialize(using = IntegerBooleanDeserializer.class)
	private boolean deleted;
	@JsonProperty("hidden")
	@JsonDeserialize(using = IntegerBooleanDeserializer.class)
	private boolean hidden;
	@JsonProperty("teamid")
	private int teamId;
	@JsonProperty("firstname")
	private String firstName;
	@JsonProperty("lastname")
	private String lastName;
	@JsonProperty("nickname")
	private String nickname;
	@JsonProperty("url")
	private String url;
	@JsonProperty("birthday")
	private long birthday;//TODO
	@JsonProperty("age")
	private Integer age;
	@JsonProperty("joined")
	@JsonDeserialize(using = IntegerBooleanDeserializer.class)
	private boolean joined;
	@JsonProperty("eslid")
	private String eslId;
	@JsonProperty("hometown")
	private String hometown;
	@JsonProperty("country")
	private String country;
	@JsonProperty("flag")
	private String flag;
	@JsonProperty("twitter")
	private String twitter;
	@JsonProperty("history")
	@JsonDeserialize(using = UnknownDeserializer.class)
	private Object history;
	@JsonProperty("achievements")
	@JsonDeserialize(using = UnknownDeserializer.class)
	private Object achievements;
	@JsonProperty("photo")
	private String photo;
	@JsonProperty("photo_template")
	private String photoTemplate;
	@JsonProperty("photo_portrait")
	private String photoPortrait;
	@JsonProperty("photo_square")
	private String photoSquare;
	@JsonProperty("position")
	private String position;
	@JsonProperty("favorite1")
	private String favorite1;
	@JsonProperty("favorite2")
	private String favorite2;
	@JsonProperty("favorite3")
	private String favorite3;
	@JsonProperty("gameaccount_url")
	private String gameAccountUrl;
	@JsonProperty("gameaccount_id")
	private String gameAccountId;
	@JsonProperty("gameaccount_name")
	private String gameAccountName;
	@JsonProperty("teamname")
	private String teamName;
	@JsonProperty("facebook")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL facebook;
	@JsonProperty("youtube_video")
	private String youtubeVideo;
	@JsonProperty("youtube_channel")
	private String youtubeChannel;
	@JsonProperty("twitch_channel")
	private String twitchChannel;
	@JsonProperty("qualified_by")
	private String qualifiedBy;
	@JsonProperty("category")
	private String category;
	@JsonProperty("game")
	private String game;
	@JsonProperty("disqualified")
	@JsonDeserialize(using = IntegerBooleanDeserializer.class)
	private boolean disqualified;
	@JsonProperty("outofcompetition")
	@JsonDeserialize(using = IntegerBooleanDeserializer.class)
	private boolean outOfCompetition;
	@JsonProperty("prodbkey")
	private String prodBKey;
	@JsonProperty("updateviaprodb")
	@JsonDeserialize(using = IntegerBooleanDeserializer.class)
	private boolean updateViaProdB;
	@JsonProperty("vk")
	private String vk;
	@JsonProperty("needsprodbupdate")
	@JsonDeserialize(using = IntegerBooleanDeserializer.class)
	private boolean needsProdBUpdate;
}
