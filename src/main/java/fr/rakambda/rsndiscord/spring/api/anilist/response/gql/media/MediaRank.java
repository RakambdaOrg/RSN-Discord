package fr.rakambda.rsndiscord.spring.api.anilist.response.gql.media;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MediaRank{
	private int id;
	private int rank;
	private MediaRankType type;
	private MediaFormat format;
	private Integer year;
	private MediaSeason season;
	private boolean allTime;
	private String context;
}
