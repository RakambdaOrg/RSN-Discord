package fr.raksrinana.rsndiscord.utils.eslgaming.model.matchday;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.eslgaming.model.matchday.match.*;
import fr.raksrinana.rsndiscord.utils.json.IntegerBooleanDeserializer;
import fr.raksrinana.rsndiscord.utils.json.MapInGameStatsDeserializer;
import fr.raksrinana.rsndiscord.utils.json.MatchMetaListDeserializer;
import fr.raksrinana.rsndiscord.utils.json.SQLTimestampDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class Match{
	@JsonProperty("uid")
	private int uid;
	@JsonProperty("pid")
	private int pid;
	@JsonProperty("tstamp")
	@JsonDeserialize(using = SQLTimestampDeserializer.class)
	private LocalDateTime timestamp;
	@JsonProperty("crdate")
	@JsonDeserialize(using = SQLTimestampDeserializer.class)
	private LocalDateTime createdDate;
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
	@JsonProperty("team1")
	private Team team1;
	@JsonProperty("team2")
	private Team team2;
	@JsonProperty("playdate")
	@JsonDeserialize(using = SQLTimestampDeserializer.class)
	private LocalDateTime playDate;
	@JsonProperty("winner")
	private int winner;
	@JsonProperty("result_team1")
	private int resultTeam1;
	@JsonProperty("result_team2")
	private int resultTeam2;
	@JsonProperty("result_map1_team1")
	private int resultMap1Team1;
	@JsonProperty("result_map1_team2")
	private int resultMap1Team2;
	@JsonProperty("result_map2_team1")
	private int resultMap2Team1;
	@JsonProperty("result_map2_team2")
	private int resultMap2Team2;
	@JsonProperty("result_map3_team1")
	private int resultMap3Team1;
	@JsonProperty("result_map3_team2")
	private int resultMap3Team2;
	@JsonProperty("result_map4_team1")
	private int resultMap4Team1;
	@JsonProperty("result_map4_team2")
	private int resultMap4Team2;
	@JsonProperty("result_map5_team1")
	private int resultMap5Team1;
	@JsonProperty("result_map5_team2")
	private int resultMap5Team2;
	@JsonProperty("team1_community_votes")
	private String team1CommunityVotes;
	@JsonProperty("team2_community_votes")
	private String team2CommunityVotes;
	@JsonProperty("disable_community_votes")
	@JsonDeserialize(using = IntegerBooleanDeserializer.class)
	private boolean disableCommunityVotes;
	@JsonProperty("videos")
	private String videos;
	@JsonProperty("exclude_ranking")
	@JsonDeserialize(using = IntegerBooleanDeserializer.class)
	private boolean excludeRanking;
	@JsonProperty("groupname")
	private String groupName;
	@JsonProperty("hideplaydate")
	@JsonDeserialize(using = IntegerBooleanDeserializer.class)
	private boolean hidePlayDate;
	@JsonProperty("map1")
	private String map1;
	@JsonProperty("map2")
	private String map2;
	@JsonProperty("map3")
	private String map3;
	@JsonProperty("map4")
	private String map4;
	@JsonProperty("map5")
	private String map5;
	@JsonProperty("map1_ebotmatchid")
	private int map1EbotMatchId;
	@JsonProperty("map2_ebotmatchid")
	private int map2EbotMatchId;
	@JsonProperty("map3_ebotmatchid")
	private int map3EbotMatchId;
	@JsonProperty("map4_ebotmatchid")
	private int map4EbotMatchId;
	@JsonProperty("map5_ebotmatchid")
	private int map5EbotMatchId;
	@JsonProperty("ebostatus")
	private String ebostatus;
	@JsonProperty("islive")
	@JsonDeserialize(using = IntegerBooleanDeserializer.class)
	private boolean live;
	@JsonProperty("prodbkey")
	private String prodBKey;
	@JsonProperty("map1_ingamestats")
	@JsonDeserialize(using = MapInGameStatsDeserializer.class)
	private MapInGameStats map1InGameStats;
	@JsonProperty("map2_ingamestats")
	@JsonDeserialize(using = MapInGameStatsDeserializer.class)
	private MapInGameStats map2InGameStats;
	@JsonProperty("map3_ingamestats")
	@JsonDeserialize(using = MapInGameStatsDeserializer.class)
	private MapInGameStats map3InGameStats;
	@JsonProperty("map4_ingamestats")
	@JsonDeserialize(using = MapInGameStatsDeserializer.class)
	private MapInGameStats map4InGameStats;
	@JsonProperty("map5_ingamestats")
	@JsonDeserialize(using = MapInGameStatsDeserializer.class)
	private MapInGameStats map5InGameStats;
	@JsonProperty("urldemos")
	private String urlDemos;
	@JsonProperty("eslplayid")
	private int ESLPlayId;
	@JsonProperty("round")
	private Integer round;
	@JsonProperty("livestream")
	private String livestream;
	@JsonProperty("customtext")
	private String customText;
	@JsonProperty("needsprodbupdate")
	@JsonDeserialize(using = IntegerBooleanDeserializer.class)
	private boolean needsProdBUpdate;
	@JsonProperty("tournamenttype")
	private String tournamentType;
	@JsonProperty("meta")
	@JsonDeserialize(using = MatchMetaListDeserializer.class)
	private List<MatchMeta> meta;
	@JsonProperty("predecessors")
	private String predecessors;
	@JsonProperty("player1")
	private Player player1;
	@JsonProperty("player2")
	private Player player2;
	@JsonProperty("groupname_short")
	private String groupNameShort;
	@JsonProperty("subgroupname")
	private String subGroupName;
	@JsonProperty("vods")
	private List<Vod> vods;
	
	@Override
	public int hashCode(){
		return Objects.hash(getUid());
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(!(o instanceof Match)){
			return false;
		}
		Match match = (Match) o;
		return getUid() == match.getUid();
	}
	
	public Optional<Team> getWinnerTeam(){
		switch(getWinner()){
			case 1:
				return Optional.of(getTeam1());
			case 2:
				return Optional.of(getTeam2());
		}
		return Optional.empty();
	}
}
