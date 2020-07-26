package fr.raksrinana.rsndiscord.utils.giphy.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class Images{
	@JsonProperty("fixed_height")
	private Image fixedHeight;
	@JsonProperty("fixed_height_still")
	private Image fixedHeightStill;
	@JsonProperty("fixed_height_downsampled")
	private Image fixedHeightDownsampled;
	@JsonProperty("fixed_width")
	private Image fixedWidth;
	@JsonProperty("fixed_width_still")
	private Image fixedWidthStill;
	@JsonProperty("fixed_width_downsampled")
	private Image fixedWidthDownsampled;
	@JsonProperty("fixed_height_small")
	private Image fixedHeightSmall;
	@JsonProperty("fixed_height_small_still")
	private Image fixedHeightSmallStill;
	@JsonProperty("fixed_width_small")
	private Image fixedWidthSmall;
	@JsonProperty("fixed_width_small_still")
	private Image fixedWidthSmallStill;
	@JsonProperty("downsized")
	private Image downsized;
	@JsonProperty("downsized_still")
	private Image downsizedStill;
	@JsonProperty("downsized_large")
	private Image downsizedLarge;
	@JsonProperty("downsized_medium")
	private Image downsizedMedium;
	@JsonProperty("downsized_small")
	private Image downsizedSmall;
	@JsonProperty("original")
	private Image original;
	@JsonProperty("original_still")
	private Image originalStill;
	@JsonProperty("looping")
	private Image looping;
	@JsonProperty("preview_gif")
	private Image previewGif;
}
