package fr.rakambda.rsndiscord.spring.interaction.button;

import fr.rakambda.rsndiscord.spring.interaction.button.api.IExecutableButton;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ButtonService{
	private final Map<String, IExecutableButton> executableButtons;
	
	@Autowired
	public ButtonService(Collection<IExecutableButton> executableButtons){
		this.executableButtons = executableButtons.stream()
				.collect(Collectors.toMap(IExecutableButton::getComponentId, m -> m));
	}
	
	@NonNull
	public Optional<IExecutableButton> getExecutableButton(@NonNull String componentId){
		return Optional.of(executableButtons.get(componentId));
	}
}
