package fr.raksrinana.rsndiscord.utils.eslgaming.model.matchday.match;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class MapResult{
	private final int mapNumber;
	private final String mapName;
	private final int team1Score;
	private final int team2Score;
	
	public MapResult(int mapNumber, @NonNull String mapName, int team1Score, int team2Score){
		this.mapNumber = mapNumber;
		this.mapName = mapName;
		this.team1Score = team1Score;
		this.team2Score = team2Score;
	}
}
