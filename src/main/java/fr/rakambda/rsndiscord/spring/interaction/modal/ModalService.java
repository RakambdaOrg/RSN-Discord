package fr.rakambda.rsndiscord.spring.interaction.modal;

import fr.rakambda.rsndiscord.spring.interaction.modal.api.IExecutableModal;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ModalService{
	private final Map<String, IExecutableModal> executableModals;
	
	@Autowired
	public ModalService(Collection<IExecutableModal> executableModals){
		this.executableModals = executableModals.stream()
				.collect(Collectors.toMap(IExecutableModal::getComponentId, m -> m));
	}
	
	@NonNull
	public Optional<IExecutableModal> getExecutableModal(@NonNull String componentId){
		return Optional.of(executableModals.get(componentId));
	}
}
