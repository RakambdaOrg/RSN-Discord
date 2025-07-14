package fr.rakambda.rsndiscord.spring.interaction.button.impl.weward;

import fr.rakambda.rsndiscord.spring.util.LocalizationService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.function.Supplier;

@Slf4j
@Component
public class WewardTradedButtonHandler extends WewardDeleteButtonHandler{
	private static final String COMPONENT_ID = "weward-traded";
	
	@Autowired
	public WewardTradedButtonHandler(LocalizationService localizationService){
		super(localizationService);
	}
	
	@Override
	@NotNull
	public String getComponentId(){
		return COMPONENT_ID;
	}
	
	@NotNull
	public static Supplier<Button> builder(){
		return () -> Button.success(COMPONENT_ID, "Traded").withEmoji(Emoji.fromUnicode("U+2705"));
	}
}
