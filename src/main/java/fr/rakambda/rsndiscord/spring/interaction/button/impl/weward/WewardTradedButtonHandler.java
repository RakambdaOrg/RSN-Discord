package fr.rakambda.rsndiscord.spring.interaction.button.impl.weward;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import java.util.function.Supplier;

@Slf4j
@Component
public class WewardTradedButtonHandler extends WewardDeleteButtonHandler{
	private static final String COMPONENT_ID = "weward-traded";
	
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
