package fr.raksrinana.rsndiscord.api.pandascore;

import fr.raksrinana.rsndiscord.log.Log;
import kong.unirest.HeaderNames;
import kong.unirest.Unirest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public class PandaScoreApi {
    private static final String ENDPOINT = "https://api.pandascore.co";
    private static String BEARER;

    @Nullable
    private static String getBearerToken() {
        if (Objects.isNull(BEARER)) {
            BEARER = System.getProperty("PANDASCORE_BEARER");
        }
        return BEARER;
    }

    @NotNull
    public static <T> Optional<T> executeGetRequest(@NotNull PandaScoreGetRequest<T> request) {
        var response = Unirest.get(ENDPOINT + request.getEndpoint())
                .header(HeaderNames.AUTHORIZATION, "Bearer " + getBearerToken())
                .queryString(request.getQueryParameters())
                .asObject(request.getGenericType());

        if (!response.isSuccess()) {
            Log.getLogger().error("Failed to perform request on PandaScore");
            return Optional.empty();
        }

        return Optional.ofNullable(response.getBody());
    }
}
