package fr.raksrinana.rsndiscord.api.pandascore;

import fr.raksrinana.rsndiscord.utils.Utilities;
import kong.unirest.core.HeaderNames;
import kong.unirest.core.Unirest;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Objects;
import java.util.Optional;

@Log4j2
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
            if(response.getStatus() != 500){
                response.getParsingError().ifPresent(error -> {
                    Utilities.reportException("Failed to parse PandaScore response", error);
                    log.warn("Failed to parse PandaScore response", error);
                });
            }
            log.error("Failed to perform request on PandaScore with status {}", response.getStatus());
            return Optional.empty();
        }

        return Optional.ofNullable(response.getBody());
    }
}
