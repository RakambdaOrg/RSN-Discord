package fr.raksrinana.rsndiscord.utils.thecatapi.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class Breed{
	@JsonProperty("id")
	private String id;
	@JsonProperty("name")
	private String name;
	@JsonProperty("temperament")
	private String temperament;
	@JsonProperty("life_span")
	private String lifeSpan;
	@JsonProperty("alt_names")
	private String altNames;
	@JsonProperty("wikipedia_url")
	private String wikipediaUrl;
	@JsonProperty("origin")
	private String origin;
	@JsonProperty("weight_imperial")
	private String weightImperial;
	@JsonProperty("experimental")
	private int experimental;
	@JsonProperty("hairless")
	private int hairless;
	@JsonProperty("natural")
	private int natural;
	@JsonProperty("rare")
	private int rare;
	@JsonProperty("rex")
	private int rex;
	@JsonProperty("suppress_tail")
	private int suppressTail;
	@JsonProperty("short_legs")
	private int shortLegs;
	@JsonProperty("hypoallergenic")
	private int hypoallergenic;
	@JsonProperty("adaptability")
	private int adaptability;
	@JsonProperty("affection_level")
	private int affectionLevel;
	@JsonProperty("country_code")
	private String countryCode;
	@JsonProperty("child_friendly")
	private int childFriendly;
	@JsonProperty("dog_friendly")
	private int dogFriendly;
	@JsonProperty("energy_level")
	private int energyLevel;
	@JsonProperty("grooming")
	private int grooming;
	@JsonProperty("health_issues")
	private int healthIssues;
	@JsonProperty("intelligence")
	private int intelligence;
	@JsonProperty("shedding_level")
	private int sheddingLevel;
	@JsonProperty("social_needs")
	private int socialNeeds;
	@JsonProperty("stranger_friendly")
	private int strangerFriendly;
	@JsonProperty("vocalisation")
	private int vocalisation;
}
