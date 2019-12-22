package fr.raksrinana.rsndiscord;

import fr.raksrinana.rsndiscord.utils.trakt.TraktUtils;
import fr.raksrinana.rsndiscord.utils.trakt.requests.DeviceCodePostRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainTest{
	public static void main(String[] args) throws Exception{
		Main.loadEnv(args);
		final var request = new DeviceCodePostRequest();
		final var result = TraktUtils.postQuery(null, request);
		log.info("{}", result);
	}
}
