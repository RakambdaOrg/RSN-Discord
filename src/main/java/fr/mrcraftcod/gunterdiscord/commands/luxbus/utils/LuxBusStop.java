package fr.mrcraftcod.gunterdiscord.commands.luxbus.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LuxBusStop{
	private static final Logger LOGGER = LoggerFactory.getLogger(LuxBusStop.class);
	private final String id;
	private final String name;
	private final float x;
	private final float y;
	private final int u;
	private final int l;
	private final int b;
	private final int p;
	private final int a;
	
	private LuxBusStop(@NotNull final String id){
		final var symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator(',');
		final var format = new DecimalFormat("0.#");
		format.setDecimalFormatSymbols(symbols);
		this.id = id.trim();
		this.name = Arrays.stream(id.split("@")).filter(str -> str.startsWith("O=")).map(str -> str.substring("O=".length())).findFirst().orElse("Unknown name");
		this.x = Arrays.stream(id.split("@")).filter(str -> str.startsWith("X=")).map(str -> str.substring("X=".length())).map(str -> {
			try{
				return format.parse(str).floatValue();
			}
			catch(ParseException e){
				LOGGER.warn("Failed to parse float {}", str, e);
			}
			return null;
		}).filter(Objects::nonNull).findFirst().orElse(0F);
		this.y = Arrays.stream(id.split("@")).filter(str -> str.startsWith("Y=")).map(str -> str.substring("Y=".length())).map(str -> {
			try{
				return format.parse(str).floatValue();
			}
			catch(ParseException e){
				LOGGER.warn("Failed to parse float {}", str, e);
			}
			return null;
		}).filter(Objects::nonNull).findFirst().orElse(0F);
		this.u = Arrays.stream(id.split("@")).filter(str -> str.startsWith("U=")).map(str -> str.substring("U=".length())).map(Integer::parseInt).findFirst().orElse(0);
		this.l = Arrays.stream(id.split("@")).filter(str -> str.startsWith("L=")).map(str -> str.substring("L=".length())).map(Integer::parseInt).findFirst().orElse(0);
		this.b = Arrays.stream(id.split("@")).filter(str -> str.startsWith("B=")).map(str -> str.substring("B=".length())).map(Integer::parseInt).findFirst().orElse(0);
		this.p = Arrays.stream(id.split("@")).filter(str -> str.startsWith("p=")).map(str -> str.substring("p=".length())).map(Integer::parseInt).findFirst().orElse(0);
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
		if(other == null || getClass() != other.getClass()){
			return false;
		}
		final var that = (LuxBusStop) other;
		return this.l == that.l && this.name.equals(that.name);
	}
	
	@Override
	public String toString(){
		return this.getName();
	}
	
	public boolean isStop(@NotNull final String id){
		final var fields = Arrays.stream(id.split("@")).filter(s -> !s.isBlank() && s.contains("=")).map(String::trim).collect(Collectors.toMap(s -> s.substring(0, s.indexOf("=")), s -> s.substring(s.indexOf("=") + 1)));
		return Objects.equals(this.getName(), fields.getOrDefault("O", null)) && Objects.equals("" + this.getL(), fields.getOrDefault("L", null));
	}
	
	public String getName(){
		return this.name;
	}
	
	public int getL(){
		return this.l;
	}
	
	public int getA(){
		return this.a;
	}
	
	public int getB(){
		return this.b;
	}
	
	public String getId(){
		return this.id;
	}
	
	public int getP(){
		return this.p;
	}
	
	public int getU(){
		return this.u;
	}
	
	public float getX(){
		return this.x;
	}
	
	public float getY(){
		return this.y;
	}
}
