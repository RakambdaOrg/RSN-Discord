package fr.raksrinana.rsndiscord.api.pandascore.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
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
    public Status getByName(@Nullable String name) {
        for (var status : Status.values()) {
            if (status.name().equalsIgnoreCase(name)) {
                return status;
            }
        }
        return null;
    }
}
