package fr.raksrinana.rsndiscord.utils.overwatch.year2020.content.meta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.utils.overwatch.year2020.content.meta.dropdown.Dropdown;
import fr.raksrinana.rsndiscord.utils.overwatch.year2020.content.meta.tab.Tab;
import fr.raksrinana.rsndiscord.utils.overwatch.year2020.content.utils.asset.Asset;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.Map;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class Meta{
	@JsonProperty("version")
	private String version;
	@JsonProperty("assets")
	private Map<String, Asset> assets;
	@JsonProperty("strings")
	private Map<String, Object> strings;
	@JsonProperty("dropdowns")
	private Set<Dropdown> dropdowns;
	@JsonProperty("tabs")
	private Set<Tab> tabs;
}
