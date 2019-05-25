package fr.mrcraftcod.gunterdiscord.commands.luxbus.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
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
	private final String ID;
	private final String name;
	private final float x;
	private final float y;
	private final int u;
	private final int l;
	private final int b;
	private final int p;
	private final int a;
	
	private LuxBusStop(final String id){
		final var symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator(',');
		final var format = new DecimalFormat("0.#");
		format.setDecimalFormatSymbols(symbols);
		
		this.ID = id.trim();
		this.name = Arrays.stream(id.split("@")).filter(s -> s.startsWith("O=")).map(s -> s.substring("O=".length())).findFirst().orElse("Unknown name");
		this.x = Arrays.stream(id.split("@")).filter(s -> s.startsWith("X=")).map(s -> s.substring("X=".length())).map(s -> {
			try{
				return format.parse(s).floatValue();
			}
			catch(ParseException e){
				LOGGER.warn("Failed to parse float {}", s, e);
			}
			return null;
		}).filter(Objects::nonNull).findFirst().orElse(0F);
		this.y = Arrays.stream(id.split("@")).filter(s -> s.startsWith("Y=")).map(s -> s.substring("Y=".length())).map(s -> {
			try{
				return format.parse(s).floatValue();
			}
			catch(ParseException e){
				LOGGER.warn("Failed to parse float {}", s, e);
			}
			return null;
		}).filter(Objects::nonNull).findFirst().orElse(0F);
		this.u = Arrays.stream(id.split("@")).filter(s -> s.startsWith("U=")).map(s -> s.substring("U=".length())).map(Integer::parseInt).findFirst().orElse(0);
		this.l = Arrays.stream(id.split("@")).filter(s -> s.startsWith("L=")).map(s -> s.substring("L=".length())).map(Integer::parseInt).findFirst().orElse(0);
		this.b = Arrays.stream(id.split("@")).filter(s -> s.startsWith("B=")).map(s -> s.substring("B=".length())).map(Integer::parseInt).findFirst().orElse(0);
		this.p = Arrays.stream(id.split("@")).filter(s -> s.startsWith("p=")).map(s -> s.substring("p=".length())).map(Integer::parseInt).findFirst().orElse(0);
		this.a = Arrays.stream(id.split("@")).filter(s -> s.startsWith("A=")).map(s -> s.substring("A=".length())).map(Integer::parseInt).findFirst().orElse(0);
	}
	
	@JsonCreator
	public static LuxBusStop createStop(final String ID){
		if(Objects.isNull(ID) || ID.isBlank()){
			throw new IllegalArgumentException("Stop id must not be blank");
		}
		final var tempStop = new LuxBusStop(ID);
		return LuxBusUtils.getStopIDs().values().stream().filter(s -> Objects.equals(s, tempStop)).findFirst().orElse(tempStop);
	}
	
	@Override
	public int hashCode(){
		return Objects.hash(this.ID);
	}
	
	@Override
	public boolean equals(final Object o){
		if(this == o){
			return true;
		}
		if(o == null || getClass() != o.getClass()){
			return false;
		}
		final var that = (LuxBusStop) o;
		return this.l == that.l && this.name.equals(that.name);
	}
	
	@Override
	public String toString(){
		return this.getName();
	}
	
	public boolean isStop(final String id){
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
	
	public String getID(){
		return this.ID;
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
