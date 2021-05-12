package fr.raksrinana.rsndiscord.api.pandascore.request;

import fr.raksrinana.rsndiscord.api.pandascore.PandaScoreGetRequest;
import fr.raksrinana.rsndiscord.api.pandascore.data.match.RainbowSixMatch;
import kong.unirest.GenericType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GetRunningR6Matches extends PandaScoreGetRequest<List<RainbowSixMatch>> {
    @Override
    public @NotNull String getEndpoint() {
        return "/r6siege/matches/running";
    }

    @Override
    public @NotNull GenericType<List<RainbowSixMatch>> getGenericType() {
        return new GenericType<>() {
        };
    }
}
