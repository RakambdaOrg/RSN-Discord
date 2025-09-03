package fr.rakambda.rsndiscord.spring.api.simkl.response.history;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Show {
	@NonNull
	private String title;
	private int year;
	@NonNull
	private MediaIds ids;
	@Nullable
	private String poster;
	@Nullable
	private Season season;
}
