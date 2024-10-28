package fr.rakambda.rsndiscord.spring.interaction.slash.impl.waward;

import fr.rakambda.rsndiscord.spring.api.exceptions.RequestFailedException;
import fr.rakambda.rsndiscord.spring.interaction.button.impl.weward.WewardDeleteButtonHandler;
import fr.rakambda.rsndiscord.spring.interaction.button.impl.weward.WewardInterestButtonHandler;
import fr.rakambda.rsndiscord.spring.interaction.slash.api.IExecutableSlashCommandGuild;
import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import fr.rakambda.rsndiscord.spring.util.LocalizationService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@Component(value = "wewardAddCommand")
@Slf4j
public class AddCommand implements IExecutableSlashCommandGuild{
	public static final String PACK_OPTION_ID = "pack";
	public static final String NAME_OPTION_ID = "name";
	
	private static final ItemComponent[] BUTTONS_CARD = {
			WewardInterestButtonHandler.builder().get(),
			WewardDeleteButtonHandler.builder().get()
	};
	
	private final LocalizationService localizationService;
	private final Collection<String> packs;
	
	@Autowired
	public AddCommand(LocalizationService localizationService){
		this.localizationService = localizationService;
		
		packs = new LinkedHashSet<>();
		packs.add("Australia");
		packs.add("Barcelona");
		packs.add("Berlin");
		packs.add("Brussels");
		packs.add("Buenos Aires");
		packs.add("Egypt");
		packs.add("Finland");
		packs.add("Global Walk Cup");
		packs.add("Greece");
		packs.add("Hawaii");
		packs.add("India");
		packs.add("Jordan");
		packs.add("London");
		packs.add("Mexico City");
		packs.add("Mongolia");
		packs.add("Montreal");
		packs.add("Morocco");
		packs.add("New Orleans");
		packs.add("New York");
		packs.add("Oslo");
		packs.add("Paris");
		packs.add("Peru");
		packs.add("Portugal");
		packs.add("Rio de Janeiro");
		packs.add("Rome");
		packs.add("Scotland");
		packs.add("Seoul");
		packs.add("South Africa");
		packs.add("Tokyo");
		packs.add("Venice");
		packs.add("Vietnam");
	}
	
	@Override
	@NotNull
	public String getId(){
		return "add";
	}
	
	@Override
	@NotNull
	public String getPath(){
		return "weward/add";
	}
	
	@Override
	@NotNull
	public CompletableFuture<?> autoCompleteGuild(@NotNull CommandAutoCompleteInteractionEvent event, @NotNull Guild guild, @NotNull Member member){
		return switch(event.getFocusedOption().getName()){
			case PACK_OPTION_ID -> autoCompletePack(event);
			default -> CompletableFuture.completedFuture(null);
		};
	}
	
	@NotNull
	private CompletableFuture<Void> autoCompletePack(@NotNull CommandAutoCompleteInteractionEvent event){
		var choices = getPacksStartingWith(event.getFocusedOption().getValue())
				.limit(OptionData.MAX_CHOICES)
				.sorted()
				.map(name -> new Command.Choice(name, name))
				.toList();
		return JDAWrappers.reply(event, choices).submit();
	}
	
	@NotNull
	private Stream<String> getPacksStartingWith(@NotNull String value){
		if(value.isBlank()){
			return packs.stream();
		}
		return packs.stream().filter(name -> name.toLowerCase(Locale.ROOT).startsWith(value.toLowerCase(Locale.ROOT)));
	}
	
	@Override
	@NotNull
	public CompletableFuture<?> executeGuild(@NotNull SlashCommandInteraction event, @NotNull Guild guild, @NotNull Member member) throws RequestFailedException{
		var deferred = event.deferReply(false).submit();
		var locale = event.getGuildLocale();
		
		var content = localizationService.translate(locale, "weward.card-added",
				member.getAsMention(),
				event.getOption(PACK_OPTION_ID).getAsString(),
				event.getOption(NAME_OPTION_ID).getAsString()
		);
		return deferred.thenCompose(empty -> JDAWrappers.reply(event, content)
				.addActionRow(BUTTONS_CARD)
				.submit());
	}
}
