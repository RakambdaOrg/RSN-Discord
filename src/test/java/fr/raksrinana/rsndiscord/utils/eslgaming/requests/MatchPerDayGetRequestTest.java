package fr.raksrinana.rsndiscord.utils.eslgaming.requests;

import fr.raksrinana.rsndiscord.utils.RequestException;
import fr.raksrinana.rsndiscord.utils.eslgaming.ESLUtils;
import fr.raksrinana.rsndiscord.utils.eslgaming.model.generic.ESLRegion;
import fr.raksrinana.utils.http.JacksonObjectMapper;
import kong.unirest.Unirest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class MatchPerDayGetRequestTest{
	@BeforeAll
	static void beforeAll(){
		Unirest.config().setObjectMapper(new JacksonObjectMapper()).connectTimeout(30000).socketTimeout(30000).enableCookieManagement(true).verifySsl(true);
	}
	
	@AfterAll
	static void afterAll(){
		Unirest.shutDown();
	}
	
	@ParameterizedTest(name = "Testing match per day request for region {0}")
	@EnumSource(ESLRegion.class)
	public void testRequest200(ESLRegion region) throws RequestException{
		final var request = new MatchPerDayGetRequest(region);
		final var result = ESLUtils.getQuery(request);
		Assertions.assertNotNull(result, "Result should not be null");
	}
}
