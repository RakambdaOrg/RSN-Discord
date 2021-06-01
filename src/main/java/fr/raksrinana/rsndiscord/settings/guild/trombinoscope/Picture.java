package fr.raksrinana.rsndiscord.settings.guild.trombinoscope;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.raksrinana.rsndiscord.settings.IAtomicConfiguration;
import fr.raksrinana.rsndiscord.utils.json.converter.ZonedDateTimeDeserializer;
import fr.raksrinana.rsndiscord.utils.json.converter.ZonedDateTimeSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class Picture implements IAtomicConfiguration{
	@JsonProperty("uuid")
	private UUID uuid;
	@JsonProperty("path")
	private Path path;
	@JsonProperty("date")
	@JsonDeserialize(using = ZonedDateTimeDeserializer.class)
	@JsonSerialize(using = ZonedDateTimeSerializer.class)
	private ZonedDateTime date;
	
	public Picture(@NotNull Path path, @NotNull ZonedDateTime date){
		uuid = UUID.randomUUID();
		this.path = path;
		this.date = date;
	}
	
	@Override
	public boolean shouldBeRemoved(){
		return false;
	}
}
