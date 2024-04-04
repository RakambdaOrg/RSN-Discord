package fr.rakambda.rsndiscord.spring.api;

import fr.rakambda.rsndiscord.spring.api.exceptions.RequestFailedException;
import fr.rakambda.rsndiscord.spring.api.exceptions.StatusCodeException;
import io.netty.handler.logging.LogLevel;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HttpUtils{
	@Nullable
	public static <T> T withStatusOk(@NotNull ResponseEntity<T> entity) throws StatusCodeException{
		if(!entity.getStatusCode().is2xxSuccessful()){
			throw new StatusCodeException(entity.getStatusCode());
		}
		return entity.getBody();
	}
	
	@NotNull
	public static <T> T withStatusOkAndBody(@NotNull ResponseEntity<T> entity) throws RequestFailedException{
		return verifyBody(verifyStatus(entity)).getBody();
	}
	
	@NotNull
	public static <T> ResponseEntity<T> verifyStatus(@NotNull ResponseEntity<T> entity) throws StatusCodeException{
		if(!entity.getStatusCode().is2xxSuccessful()){
			throw new StatusCodeException(entity.getStatusCode());
		}
		return entity;
	}
	
	@NotNull
	public static <T> ResponseEntity<T> verifyBody(@NotNull ResponseEntity<T> entity) throws RequestFailedException{
		if(Objects.isNull(entity.getBody())){
			throw new RequestFailedException("No body received");
		}
		return entity;
	}
	
	@NotNull
	public static ClientHttpConnector wiretapClientConnector(@NotNull Class<?> clazz){
		return new ReactorClientHttpConnector(HttpClient.create().wiretap(clazz.getCanonicalName(), LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL));
	}
	
	@NotNull
	public static ExchangeFilterFunction logErrorFilter(){
		return logErrorFilter(Set.of());
	}
	
	@NotNull
	public static ExchangeFilterFunction logErrorFilter(@NotNull Collection<HttpStatusCode> ignoreStatuses){
		return ExchangeFilterFunction.ofResponseProcessor(response -> {
			if(response.statusCode().isError() && !ignoreStatuses.contains(response.statusCode())){
				return response.bodyToMono(String.class)
						.flatMap(body -> {
							log.error("Request {} {} failed with status code {} and body {}",
									response.request().getMethod(),
									response.request().getURI(),
									response.statusCode(),
									body
							);
							return Mono.just(response);
						});
			}
			return Mono.just(response);
		});
	}
}
