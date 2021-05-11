package fr.raksrinana.rsndiscord.api.pandascore.request;

import fr.raksrinana.rsndiscord.api.pandascore.PandaScoreGetRequest;
import fr.raksrinana.rsndiscord.api.pandascore.data.R6Match;
import kong.unirest.GenericType;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class GetR6MatchBySlug extends PandaScoreGetRequest<List<R6Match>> {
    @NotNull
    private final String slug;

    @Override
    public @NotNull String getEndpoint() {
        return "/r6siege/matches";
    }

    @Override
    public @NotNull GenericType<List<R6Match>> getGenericType() {
        return new GenericType<>() {
        };
    }

    @Override
    public @NotNull Map<String, Object> getQueryParameters() {
        return Map.of(
                "search[slug]", slug
        );
    }
}
