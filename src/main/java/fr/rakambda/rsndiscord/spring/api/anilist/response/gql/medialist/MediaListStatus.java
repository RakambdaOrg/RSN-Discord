package fr.rakambda.rsndiscord.spring.api.anilist.response.gql.medialist;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.awt.Color;

@RequiredArgsConstructor
@Getter
public enum MediaListStatus{
	PLANNING(Color.PINK, "Planning"),
	CURRENT(Color.ORANGE, "Current"),
	COMPLETED(Color.GREEN, "Completed"),
	DROPPED(Color.RED, "Dropped"),
	PAUSED(Color.YELLOW, "Paused"),
	REPEATING(Color.BLUE, "Repeating"),
	UNKNOWN(Color.MAGENTA, "Unknown");
	
	private final Color color;
	private final String value;
}
