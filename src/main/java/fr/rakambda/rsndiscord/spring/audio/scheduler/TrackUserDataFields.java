package fr.rakambda.rsndiscord.spring.audio.scheduler;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrackUserDataFields{
	@Builder.Default
	private boolean repeat = false;
	private Long requesterId;
}
