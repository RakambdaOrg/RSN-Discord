package fr.raksrinana.rsndiscord.api.pandascore;

import kong.unirest.GenericType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface PandaScoreRequest<T> {
    @NotNull
    String getEndpoint();

    @NotNull
    GenericType<T> getGenericType();

    @NotNull
    default Map<String, Object> getQueryParameters(){
        return Map.of();
    }
}
