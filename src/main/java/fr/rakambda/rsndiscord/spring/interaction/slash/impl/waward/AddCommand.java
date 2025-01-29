package fr.rakambda.rsndiscord.spring.interaction.slash.impl.waward;

import fr.rakambda.rsndiscord.spring.api.exceptions.RequestFailedException;
import fr.rakambda.rsndiscord.spring.interaction.button.impl.weward.WewardDeleteButtonHandler;
import fr.rakambda.rsndiscord.spring.interaction.button.impl.weward.WewardInterestButtonHandler;
import fr.rakambda.rsndiscord.spring.interaction.slash.api.IExecutableSlashCommandGuild;
import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import fr.rakambda.rsndiscord.spring.util.LocalizationService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
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
	private final Map<String, Pack> packs;
	
	@Autowired
	public AddCommand(LocalizationService localizationService){
		this.localizationService = localizationService;
		
		packs = new HashMap<>();
		addPack(new Pack("Australia",
				new Card("Sydney Opera House", 1),
				new Card("Kangaroo Boxing", 1),
				new Card("Coral Reef Snorkeling", 1),
				new Card("Sydney Harbour Bridge", 2),
				new Card("Aboriginal Drawing", 2),
				new Card("Koala Bear", 2),
				new Card("Akubra Hat", 3),
				new Card("Kings Canyon", 3),
				new Card("Surfer Wardy", 4)
		));
		addPack(new Pack("Barcelona",
				new Card("Park Güell Wildlife", 1),
				new Card("Sónar Music Festival", 1),
				new Card("Traditional Tapas", 1),
				new Card("Sagrada Família", 2),
				new Card("Flamenco Fan", 2),
				new Card("Camp Nou Stadium", 2),
				new Card("Terre Glòries", 3),
				new Card("Boqueria Market", 3),
				new Card("Dalí Wardy", 4)
		));
		addPack(new Pack("Berlin",
				new Card("Tiergarten Park", 1),
				new Card("Street Art", 1),
				new Card("Traditional Currywurst", 1),
				new Card("Berlin Wall Memorial", 2),
				new Card("Museum Island", 2),
				new Card("Spree River Scene", 2),
				new Card("Ampelmann", 3),
				new Card("Friedrichshain Stroll", 3),
				new Card("TBD", 4)
		));
		addPack(new Pack("Boston",
				new Card("TBD", 1),
				new Card("TBD", 1),
				new Card("TBD", 1),
				new Card("TBD", 2),
				new Card("BoSox Cap", 2),
				new Card("TBD", 2),
				new Card("TBD", 3),
				new Card("TBD", 3),
				new Card("Graduate Wardy", 4)
		));
		addPack(new Pack("Brussels",
				new Card("Cambre Woods", 1),
				new Card("Belgian Beer Weekend", 1),
				new Card("Belgian Chips", 1),
				new Card("Grand-Place", 2),
				new Card("Comic Book Mural", 2),
				new Card("National Dress", 2),
				new Card("Atomium", 3),
				new Card("Belgian Waffles", 3),
				new Card("Surreal Wardy", 4)
		));
		addPack(new Pack("Buenos Aires",
				new Card("Pampas", 1),
				new Card("Tango Festival", 1),
				new Card("Empanadas", 1),
				new Card("San Telmo Market", 2),
				new Card("Mate Bowl", 2),
				new Card("Caminto Street", 2),
				new Card("Art Stroll", 3),
				new Card("Mataderos Market", 3),
				new Card("TBD", 4)
		));
		addPack(new Pack("Christmas",
				new Card("Santa's Reindeer", 1),
				new Card("Christmas Market", 1),
				new Card("Gingerbread man", 1),
				new Card("Snowshoeing Adventure", 2),
				new Card("Christmas Ornament", 2),
				new Card("Santa's Village", 2),
				new Card("Gift Hunt Under the Tree", 3),
				new Card("Christmas Light Park", 3),
				new Card("TBD", 4)
		));
		addPack(new Pack("Colombia",
				new Card("Coffee plantation", 1),
				new Card("TBD", 1),
				new Card("Grilled Arepas", 1),
				new Card("Tayrona Park", 2),
				new Card("TBD", 2),
				new Card("Cartagena's Old Town", 2),
				new Card("TBD", 3),
				new Card("TBD", 3),
				new Card("TBD", 4)
		));
		addPack(new Pack("Cuba",
				new Card("Cuban Trogon", 1),
				new Card("Fiesta del Tambor", 1),
				new Card("Moros y Cristianos", 1),
				new Card("Rumba Dance", 2),
				new Card("Maracas", 2),
				new Card("Varadero Beach", 2),
				new Card("TBD", 3),
				new Card("Old Havana", 3),
				new Card("TBD", 4)
		));
		addPack(new Pack("Egypt",
				new Card("Pyramids of Giza", 1),
				new Card("Luxor", 1),
				new Card("Abu Simbel", 1),
				new Card("TBD", 2),
				new Card("Cairo Souk", 2),
				new Card("The Great Sphinx", 2),
				new Card("Siwa Oasis", 3),
				new Card("TBD", 3),
				new Card("TBD", 4)
		));
		addPack(new Pack("Finland",
				new Card("Aurora Borealis", 1),
				new Card("Finnish Islands", 1),
				new Card("Winter Holiday", 1),
				new Card("Reindeer Sledding", 2),
				new Card("Wooden Town", 2),
				new Card("Karelian Pie", 2),
				new Card("TBD", 3),
				new Card("Snow Hotel", 3),
				new Card("TBD", 4)
		));
		addPack(new Pack("Global Walk Cup",
				new Card("Fan Zone", 1),
				new Card("Opening Ceremony", 1),
				new Card("Energy Drink", 1),
				new Card("World Record", 2),
				new Card("Legendary Trainers", 2),
				new Card("Protein Bar", 2),
				new Card("Walk Championship", 3),
				new Card("TBD", 3),
				new Card("Medalist Wardy", 4)
		));
		addPack(new Pack("Greece",
				new Card("Naxos Dolphins", 1),
				new Card("Epidaurus Festival", 1),
				new Card("Moussaka", 1),
				new Card("Santorini by night", 2),
				new Card("Greek Pottery", 2),
				new Card("Olympia", 2),
				new Card("Acropolis Visit", 3),
				new Card("Paradise Beaches", 3),
				new Card("TBD", 4)
		));
		addPack(new Pack("Halloween",
				new Card("Grimalkin", 1),
				new Card("Trick or Treat", 1),
				new Card("Bloody Mary", 1),
				new Card("Haunted Mansion", 2),
				new Card("TBD", 2),
				new Card("TBD", 2),
				new Card("TBD", 3),
				new Card("Spooky Catacombs", 3),
				new Card("TBD", 4)
		));
		addPack(new Pack("Hawaii",
				new Card("Hibiscus Flower", 1),
				new Card("Hula Dance", 1),
				new Card("Hawaiian Pineapple", 1),
				new Card("Waikiki Beach", 2),
				new Card("Hawaiian Ukulele", 2),
				new Card("Sea Turtle", 2),
				new Card("Banzai Pipeline", 3),
				new Card("TBD", 3),
				new Card("TBD", 4)
		));
		addPack(new Pack("India",
				new Card("Holy Cow", 1),
				new Card("Holi Festival", 1),
				new Card("Tikka Masala", 1),
				new Card("Himalayan Trek", 2),
				new Card("Marble Elephant", 2),
				new Card("Taj Mahal", 2),
				new Card("TBD", 3),
				new Card("Lotus Temple", 3),
				new Card("TBD", 4)
		));
		addPack(new Pack("Jordan",
				new Card("Black Desert Iris", 1),
				new Card("Petra by Night", 1),
				new Card("Knafeh", 1),
				new Card("Hiking in Wadi Rum", 2),
				new Card("Madaba Mosaics", 2),
				new Card("Amman Citadel", 2),
				new Card("Dead Sea Bath", 3),
				new Card("Dana Valley", 3),
				new Card("Bedouin Wardy", 4)
		));
		addPack(new Pack("Kuala Lumpur",
				new Card("Bamboo Forest", 1),
				new Card("Thaipusam", 1),
				new Card("Nasi Lemak", 1),
				new Card("Batu Caves", 2),
				new Card("Moon Kite", 2),
				new Card("Petronas Twin Towers", 2),
				new Card("Batik Learning", 3),
				new Card("City Center Stroll", 3),
				new Card("Monkey Lover Wardy", 4)
		));
		addPack(new Pack("London",
				new Card("River Thames", 1),
				new Card("Thames Festival", 1),
				new Card("Afternoon Tea", 1),
				new Card("Big Ben", 2),
				new Card("Red Phone Box", 2),
				new Card("London Eye", 2),
				new Card("Picadilly Stroll", 3),
				new Card("TBD", 3),
				new Card("King's Guard Wardy", 4)
		));
		addPack(new Pack("Marseille",
				new Card("TBD", 1),
				new Card("TBD", 1),
				new Card("TBD", 1),
				new Card("TBD", 2),
				new Card("Ferry Boat", 2),
				new Card("TBD", 2),
				new Card("TBD", 3),
				new Card("TBD", 3),
				new Card("TBD", 4)
		));
		addPack(new Pack("Mexico City",
				new Card("Hummingbird", 1),
				new Card("Día de los Muertos", 1),
				new Card("Spicy Tacos", 1),
				new Card("Frida Kahlo Museum", 2),
				new Card("Sombrero", 2),
				new Card("Xochimilco's canals", 2),
				new Card("La Roma Neighborhood", 3),
				new Card("The Angel of Independence", 3),
				new Card("Luchador Wardy", 4)
		));
		addPack(new Pack("Mongolia",
				new Card("Yak Ride", 1),
				new Card("Golden Eagle Festival", 1),
				new Card("Khuushuur", 1),
				new Card("Gobi Desert", 2),
				new Card("Yurt", 2),
				new Card("Gandan Monastery", 2),
				new Card("Terelj Park", 3),
				new Card("Orkhon Valley", 3),
				new Card("TBD", 4)
		));
		addPack(new Pack("Montreal",
				new Card("Maple Leaf", 1),
				new Card("Nuit Blanche", 1),
				new Card("Sugar Shack Pancakes", 1),
				new Card("Montreal Poutine", 2),
				new Card("Ice Skates", 2),
				new Card("Old Montreal", 2),
				new Card("Mont Royal Hike", 3),
				new Card("Old Port of Montreal", 3),
				new Card("TBD", 4)
		));
		addPack(new Pack("Morocco",
				new Card("Sand Cat", 1),
				new Card("Rose Festival", 1),
				new Card("Chicken Tagine", 1),
				new Card("Chefchaouen Medina", 2),
				new Card("Moroccan Slippers", 2),
				new Card("Ouzoud Waterfalls", 2),
				new Card("Souk Stroll", 3),
				new Card("Merzouga Dunes", 3),
				new Card("TBD", 4)
		));
		addPack(new Pack("New Orleans",
				new Card("St. Louis Cathedral", 1),
				new Card("Jazz Festival", 1),
				new Card("Spicy Gumbo", 1),
				new Card("Mardi Gras", 2),
				new Card("Streetcar Ride", 2),
				new Card("Mississippi River", 2),
				new Card("TBD", 3),
				new Card("Steamboat Cruise", 3),
				new Card("Jazz Wardy", 4)
		));
		addPack(new Pack("New York",
				new Card("Central Park Lake", 1),
				new Card("Statue of Liberty", 1),
				new Card("Hot Dog Stand", 1),
				new Card("Yankees Hat", 2),
				new Card("Winter Squirrel", 2),
				new Card("Empire State Building", 2),
				new Card("Times Square", 3),
				new Card("Soul Singer", 3),
				new Card("TBD", 4)
		));
		addPack(new Pack("Oslo",
				new Card("Daffodils Field", 1),
				new Card("Folklore Night", 1),
				new Card("Smørrebrød", 1),
				new Card("Oslofjord Stroll", 2),
				new Card("Wooden House", 2),
				new Card("Oslo Opera", 2),
				new Card("Vigeland Park", 3),
				new Card("Akershus Fortress", 3),
				new Card("TBD", 4)
		));
		addPack(new Pack("Paris",
				new Card("Cats of Montmartre", 1),
				new Card("Fashion Week", 1),
				new Card("Butter Croissant", 1),
				new Card("Coffee break", 2),
				new Card("French Beret", 2),
				new Card("Eiffel Tower Lights", 2),
				new Card("Seine River Stroll", 3),
				new Card("Louvre Pyramid", 3),
				new Card("Mine Wardy", 4)
		));
		addPack(new Pack("Peru",
				new Card("Manú National Park", 1),
				new Card("Sun Festival", 1),
				new Card("Ceviche", 1),
				new Card("Cusco Cathedral", 2),
				new Card("Peruvian Pan Flute", 2),
				new Card("Maras Salt Mines", 2),
				new Card("Machu Picchu", 3),
				new Card("TBD", 3),
				new Card("Peruvian Shepherd Wardy", 4)
		));
		addPack(new Pack("Portugal",
				new Card("Rooster of Barcelos", 1),
				new Card("Festa de São João", 1),
				new Card("Pastéis de Nata", 1),
				new Card("Surfing in Nazaré", 2),
				new Card("Azulejos", 2),
				new Card("Sintra Palace", 2),
				new Card("Kayak on the Douro", 3),
				new Card("TBD", 3),
				new Card("TBD", 4)
		));
		addPack(new Pack("Reunion Island",
				new Card("Bird of Paradise", 1),
				new Card("La Diagonale des Fous", 1),
				new Card("Vanilla", 1),
				new Card("TBD", 2),
				new Card("Woven Bowl", 2),
				new Card("Piton de la Fournaise", 2),
				new Card("Waterfall Swimming", 3),
				new Card("Cirques Hiking", 3),
				new Card("Chillin' Wardy", 4)
		));
		addPack(new Pack("Rio de Janeiro",
				new Card("Carnival", 1),
				new Card("Sugar Loaf Cable Car", 1),
				new Card("Christ the Redeemer", 1),
				new Card("Wild Macaw", 2),
				new Card("Selarón Steps", 2),
				new Card("Ipanema beach", 2),
				new Card("Beach Flip-Flops", 3),
				new Card("Carnival Dancer", 3),
				new Card("TBD", 4)
		));
		addPack(new Pack("Rome",
				new Card("Villa Borghese", 1),
				new Card("Notte Bianca", 1),
				new Card("Cacio e Pepe", 1),
				new Card("Trevi Fountain", 2),
				new Card("Vespa", 2),
				new Card("Colesseum", 2),
				new Card("TBD", 3),
				new Card("Pantheon", 3),
				new Card("TBD", 4)
		));
		addPack(new Pack("Scotland",
				new Card("Heather Hill", 1),
				new Card("Tartan Day", 1),
				new Card("Haggis", 1),
				new Card("Castle Visit", 2),
				new Card("Bagpipes", 2),
				new Card("Round of Golf", 2),
				new Card("Loch Ness", 3),
				new Card("TBD", 3),
				new Card("TBD", 4)
		));
		addPack(new Pack("Seoul",
				new Card("Cute Raccoon", 1),
				new Card("Lantern Festival", 1),
				new Card("Corn Dog", 1),
				new Card("Karaoke Night", 2),
				new Card("K-Pop Poster", 2),
				new Card("Lotte World Tower", 2),
				new Card("Hongdae Shopping", 3),
				new Card("TBD", 3),
				new Card("Hanbok Wardy", 4)
		));
		addPack(new Pack("South Africa",
				new Card("Speedy Springbok", 1),
				new Card("Rugby Match", 1),
				new Card("Rooibos Tea", 1),
				new Card("Table Mountain", 2),
				new Card("Vuvuzela Horn", 2),
				new Card("Bo-Kaap Stroll", 2),
				new Card("Lion's Head Hike", 3),
				new Card("Kruger Park Safari", 3),
				new Card("TBD", 4)
		));
		addPack(new Pack("The Netherlands",
				new Card("Tulip Fields", 1),
				new Card("King's Day", 1),
				new Card("Gouda Cheese", 1),
				new Card("Bicycle Tour", 2),
				new Card("Delfware", 2),
				new Card("Heusden's Mills", 2),
				new Card("TBD", 3),
				new Card("TBD", 3),
				new Card("Wardy van Gogh", 4)
		));
		addPack(new Pack("Tokyo",
				new Card("Kohaku Koi", 1),
				new Card("Cherry Blossom Festival", 1),
				new Card("Udon Noodle Soup", 1),
				new Card("Tokyo Skytree", 2),
				new Card("Katana Sword", 2),
				new Card("Senso-ji Temple", 2),
				new Card("TBD", 3),
				new Card("Tea Ceremony", 3),
				new Card("TBD", 4)
		));
		addPack(new Pack("Venice",
				new Card("St. Mark's Pigeons", 1),
				new Card("Venice Carnival", 1),
				new Card("Spaghetti alle Vongole", 1),
				new Card("Gondola Ride", 2),
				new Card("Venetian Mask", 2),
				new Card("Doge's Palace", 2),
				new Card("Burano Stroll", 3),
				new Card("Rialto Bridge", 3),
				new Card("TBD", 4)
		));
		addPack(new Pack("Vietnam",
				new Card("Water Dragon", 1),
				new Card("Mid-Autumn Festival", 1),
				new Card("Hanoi-Style Pho", 1),
				new Card("Ha Long Bay Cruise", 2),
				new Card("TBD", 2),
				new Card("Hue Historic Citadel", 2),
				new Card("Rice Terraces Hike", 3),
				new Card("Golden Bridge", 3),
				new Card("TBD", 4)
		));
	}
	
	private void addPack(@NotNull Pack pack){
		packs.put(pack.name(), pack);
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
			case NAME_OPTION_ID -> autoCompleteName(event, event.getOption(PACK_OPTION_ID).getAsString());
			default -> CompletableFuture.completedFuture(null);
		};
	}
	
	@NotNull
	private CompletableFuture<Void> autoCompletePack(@NotNull CommandAutoCompleteInteractionEvent event){
		var choices = getStartingWith(packs.values(), event.getFocusedOption().getValue())
				.limit(OptionData.MAX_CHOICES)
				.sorted()
				.map(pack -> new Command.Choice(pack.name(), pack.name()))
				.toList();
		return JDAWrappers.reply(event, choices).submit();
	}
	
	@NotNull
	private CompletableFuture<Void> autoCompleteName(@NotNull CommandAutoCompleteInteractionEvent event, @NotNull String pack){
		var choices = Optional.ofNullable(packs.get(pack))
				.map(Pack::cards)
				.stream()
				.flatMap(c -> getStartingWith(c, event.getFocusedOption().getValue()))
				.limit(OptionData.MAX_CHOICES)
				.sorted()
				.map(card -> new Command.Choice("%s (%s)".formatted(card.name(), "⭐".repeat(card.stars())), card.name()))
				.toList();
		return JDAWrappers.reply(event, choices).submit();
	}
	
	@NotNull
	private <T extends INamed> Stream<T> getStartingWith(@NotNull Collection<T> values, @NotNull String value){
		if(value.isBlank()){
			return values.stream();
		}
		return values.stream().filter(v -> v.name().toLowerCase(Locale.ROOT).contains(value.toLowerCase(Locale.ROOT)));
	}
	
	@Override
	@NotNull
	public CompletableFuture<?> executeGuild(@NotNull SlashCommandInteraction event, @NotNull Guild guild, @NotNull Member member) throws RequestFailedException{
		var deferred = event.deferReply(false).submit();
		var locale = event.getGuildLocale();
		
		var pack = event.getOption(PACK_OPTION_ID).getAsString();
		var name = event.getOption(NAME_OPTION_ID).getAsString();
		var card = Optional.ofNullable(packs.get(pack)).flatMap(p -> p.getCard(name));
		
		var builder = new EmbedBuilder()
				.setTitle(localizationService.translate(locale, "weward.card-added.title"))
				.addField(localizationService.translate(locale, "weward.card-added.owner"), member.getAsMention(), false)
				.addField(localizationService.translate(locale, "weward.card-added.pack"), pack, true)
				.addField(localizationService.translate(locale, "weward.card-added.name"), name, true)
				.setFooter(member.getId());
		
		card.ifPresent(c -> builder.addField(localizationService.translate(locale, "weward.card-added.stars"), "⭐".repeat(c.stars()), true));
		
		return deferred.thenCompose(empty -> JDAWrappers.reply(event, builder.build())
				.addActionRow(BUTTONS_CARD)
				.submit());
	}
}
