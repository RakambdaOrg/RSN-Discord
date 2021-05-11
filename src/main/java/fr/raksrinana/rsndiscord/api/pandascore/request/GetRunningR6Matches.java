package fr.raksrinana.rsndiscord.api.pandascore.request;

import fr.raksrinana.rsndiscord.api.pandascore.PandaScoreGetRequest;
import fr.raksrinana.rsndiscord.api.pandascore.data.R6Match;
import kong.unirest.GenericType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GetRunningR6Matches extends PandaScoreGetRequest<List<R6Match>> {
    @Override
    public @NotNull String getEndpoint() {
        return "/r6siege/matches/running";
    }

    @Override
    public @NotNull GenericType<List<R6Match>> getGenericType() {
        return new GenericType<>() {
        };
    }
}
