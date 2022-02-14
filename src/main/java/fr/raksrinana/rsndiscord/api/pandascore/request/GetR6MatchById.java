package fr.raksrinana.rsndiscord.api.pandascore.request;

import fr.raksrinana.rsndiscord.api.pandascore.PandaScoreGetRequest;
import fr.raksrinana.rsndiscord.api.pandascore.data.match.RainbowSixMatch;
import kong.unirest.core.GenericType;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class GetR6MatchById extends PandaScoreGetRequest<List<RainbowSixMatch>> {
    private final int id;

    @Override
    public @NotNull String getEndpoint() {
        return "/r6siege/matches";
    }

    @Override
    public @NotNull GenericType<List<RainbowSixMatch>> getGenericType() {
        return new GenericType<>() {
        };
    }

    @Override
    public @NotNull Map<String, Object> getQueryParameters() {
        return Map.of(
                "filter[id]", id
        );
    }
}
