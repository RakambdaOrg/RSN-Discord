package fr.mrcraftcod.gunterdiscord.commands.luxbus.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LuxBusStop{
	private final String id;
	private final String name;
	@SuppressWarnings("StandardVariableNames")
	private final int l;
	private final int a;
	
	private LuxBusStop(@Nonnull final String id){
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
	public static LuxBusStop createStop(@Nullable final String id){
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
	public boolean equals(@Nullable final Object other){
		if(this == other){
			return true;
		}
		if(other == null || getClass() != other.getClass()){
			return false;
		}
		final var that = (LuxBusStop) other;
		return this.getL() == that.getL() && this.getName().equals(that.getName());
	}
	
	@Override
	public String toString(){
		return this.getName();
	}
	
	@Nonnull
	public String getName(){
		return this.name;
	}
	
	private int getL(){
		return this.l;
	}
	
	public int getA(){
		return this.a;
	}
	
	@Nonnull
	public String getId(){
		return this.id;
	}
}
