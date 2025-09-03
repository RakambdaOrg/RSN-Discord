package fr.rakambda.rsndiscord.spring.api.simkl.response.history;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NonNull;
import java.util.LinkedList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Season {
	private int number;
	@NonNull
	private List<Episode> episodes = new LinkedList<>();
}
