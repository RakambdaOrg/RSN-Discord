package fr.raksrinana.rsndiscord.api.pandascore.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import fr.raksrinana.rsndiscord.api.discordstatus.data.IncidentStatus;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public enum Status {
    NOT_STARTED,
    POSTPONED,
    CANCELLED,
    RUNNING,
    FINISHED;

    @JsonCreator
    @Nullable
    public IncidentStatus getByName(@Nullable String name) {
        for (var indicator : IncidentStatus.values()) {
            if (indicator.name().equalsIgnoreCase(name)) {
                return indicator;
            }
        }
        return null;
    }
}
