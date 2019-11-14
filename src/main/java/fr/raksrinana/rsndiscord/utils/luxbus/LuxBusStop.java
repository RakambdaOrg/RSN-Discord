package fr.raksrinana.rsndiscord.utils.luxbus;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NonNull;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class LuxBusStop{
	private final String id;
	private final String name;
	private final int l;
	private final int a;
	
	private LuxBusStop(@NonNull final String id){
		final var symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator(',');
		final var format = new DecimalFormat("0.#");
		format.setDecimalFormatSymbols(symbols);
		this.id = id.trim();
		this.name = Arrays.stream(id.split("@")).filter(str -> str.startsWith("O=")).map(str -> str.substring("O=".length())).findFirst().orElse("Unknown name");
		this.l = Arrays.stream(id.split("@")).filter(str -> str.startsWith("L=")).map(str -> str.substring("L=".length())).map(Integer::parseInt).findFirst().orElse(0);
		this.a = Arrays.stream(id.split("@")).filter(str -> str.startsWith("A=")).map(str -> str.substring("A=".length())).map(Integer::parseInt).findFirst().orElse(0);
	}
	
	@JsonCreator
	public static LuxBusStop createStop(final String id){
		if(Objects.isNull(id) || id.isBlank()){
			throw new IllegalArgumentException("Stop id must not be blank");
		}
		final var tempStop = new LuxBusStop(id);
		return LuxBusUtils.getStopIds().stream().filter(stop -> Objects.equals(stop, tempStop)).findFirst().orElse(tempStop);
	}
	
	@Override
	public int hashCode(){
		return Objects.hash(this.id);
	}
	
	@Override
	public boolean equals(final Object other){
		if(this == other){
			return true;
		}
		if(other == null || this.getClass() != other.getClass()){
			return false;
		}
		final var that = (LuxBusStop) other;
		return this.getL() == that.getL() && this.getName().equals(that.getName());
	}
	
	@Override
	public String toString(){
		return this.getName();
	}
}
