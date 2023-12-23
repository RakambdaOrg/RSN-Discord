package fr.rakambda.rsndiscord.spring.api.simkl.response.history;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Show {
	@NotNull
	private String title;
	private int year;
	@NotNull
	private MediaIds ids;
	@Nullable
	private String poster;
	@Nullable
	private Season season;
}
