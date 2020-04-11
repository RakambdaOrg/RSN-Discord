package fr.raksrinana.rsndiscord.utils.overwatch.year2020.content.week.event.match;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.json.UnknownDeserializer;
import fr.raksrinana.rsndiscord.utils.json.ZonedDateTimeDeserializer;
import fr.raksrinana.rsndiscord.utils.overwatch.year2020.content.utils.Link;
import fr.raksrinana.rsndiscord.utils.overwatch.year2020.content.week.event.banner.Venue;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class Match{
	@JsonProperty("id")
	private int id;
	@JsonProperty("competitors")
	private List<Competitor> competitors;
	@JsonProperty("scores")
	private List<Integer> scores;
	@JsonProperty("ordinal")
	private String ordinal;
	@JsonProperty("winnersNextMatch")
	private String winnersNextMatch;
	@JsonProperty("winnerRound")
	private String winnerRound;
	@JsonProperty("winnerOrdinal")
	private String winnerOrdinal;
	@JsonProperty("losersNextMatch")
	private String losersNextMatch;
	@JsonProperty("loserRound")
	private String loserRound;
	@JsonProperty("loserOrdinal")
	private String loserOrdinal;
	@JsonProperty("status")
	private Status status;
	@JsonProperty("startDate")
	@JsonDeserialize(using = ZonedDateTimeDeserializer.class)
	private ZonedDateTime startDate;
	@JsonProperty("endDate")
	@JsonDeserialize(using = ZonedDateTimeDeserializer.class)
	private ZonedDateTime endDate;
	@JsonProperty("home")
	private String home;
	@JsonProperty("games")
	@JsonDeserialize(contentUsing = UnknownDeserializer.class)
	private Set<Object> games;
	@JsonProperty("live")
	private boolean live;
	@JsonProperty("broadcastChannels")
	private Set<BroadcastChannel> broadcastChannels;
	@JsonProperty("tickets")
	private Ticket tickets;
	@JsonProperty("venue")
	private Venue venue;
	
	@Override
	public int hashCode(){
		return Objects.hash(getId());
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
		return getId() == match.getId();
	}
	
	public void fillEmbed(EmbedBuilder embed){
		embed.setTitle(this.getVsCompetitorsNames(), this.getBroadcastChannels().stream().map(BroadcastChannel::getLink).map(Link::getHref).map(Object::toString).findAny().orElse(null));
		embed.setTimestamp(this.getStartDate());
		embed.setDescription("Score: " + this.getScores().stream().map(Object::toString).collect(Collectors.joining(" - ")));
		embed.addField("State", this.getStatus().asString(), true);
		//FIXME: Fill embed with more infos
		// embed.addField("Conclusion strategy", this.getConclusionStrategy().asString(this), true);
		// this.getGames().forEach(game -> builder.addField("Game " + game.getNumber(), game.getAttributes().getMapName().orElse(null) + ": " + game.getPoints().stream().map(Object::toString).collect(Collectors.joining(" - ")) + game.getAttributes().getMapObject().map(o -> " (" + o.getType() + ")").orElse(""), false));
		// this.getWinningTeam().ifPresent(winnerCompetitor -> {
		// 	embed.setThumbnail(winnerCompetitor.getLogo());
		// 	embed.setImage(winnerCompetitor.getIcon());
		// 	embed.setColor(winnerCompetitor.getPrimaryColor());
		// });
		embed.setFooter("ID: " + this.getId());
	}
	
	public String getVsCompetitorsNames(){
		return this.getCompetitors().stream().map(c -> Objects.isNull(c) ? "TBD" : c.getName()).collect(Collectors.joining(" vs "));
	}
	
	@NonNull
	private Optional<Competitor> getWinningTeam(){
		int min = Math.min(this.getCompetitors().size(), this.getScores().size());
		return IntStream.range(0, min).boxed().max(Comparator.comparingInt(index -> this.getScores().get(index))).map(index -> this.getCompetitors().get(index));
	}
}
