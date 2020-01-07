package fr.raksrinana.rsndiscord.utils.amazon;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import java.net.URL;
import java.util.Optional;

public class AmazonProduct{
	@Getter
	private final URL url;
	@Getter
	private final double price;
	@Getter
	@Setter
	private URL imageUrl;
	@Setter
	private String rawPrice;
	@Getter
	@Setter
	private String name;
	
	public AmazonProduct(URL url, double price){
		this.url = url;
		this.price = price;
	}
	
	public void fillEmbed(@NonNull EmbedBuilder builder){
		Optional.ofNullable(getName()).ifPresent(name -> builder.addField("Name", name, false));
		builder.addField("Price", getRawPrice(), true);
		Optional.ofNullable(getImageUrl()).map(Object::toString).ifPresent(builder::setImage);
	}
	
	public String getRawPrice(){
		return Optional.ofNullable(rawPrice).orElseGet(() -> Double.toString(getPrice()));
	}
}
