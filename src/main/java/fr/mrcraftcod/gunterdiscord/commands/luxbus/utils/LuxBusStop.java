package fr.mrcraftcod.gunterdiscord.commands.luxbus.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator(',');
		DecimalFormat format = new DecimalFormat("0.#");
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
	public static LuxBusStop createStop(String ID){
		if(Objects.isNull(ID) || ID.isBlank()){
			throw new IllegalArgumentException("Stop id must not be blank");
		}
		final var tempStop = new LuxBusStop(ID);
		return LuxBusUtils.getStopIDs().values().stream().filter(s -> Objects.equals(s, tempStop)).findFirst().orElse(tempStop);
	}
	
	@Override
	public int hashCode(){
		return Objects.hash(ID);
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(o == null || getClass() != o.getClass()){
			return false;
		}
		LuxBusStop that = (LuxBusStop) o;
		return l == that.l && name.equals(that.name);
	}
	
	@Override
	public String toString(){
		return this.getName();
	}
	
	public String getName(){
		return name;
	}
	
	public boolean isStop(String id){
		Map<String, String> fields = Arrays.stream(id.split("@")).filter(s -> !s.isBlank() && s.contains("=")).map(String::trim).collect(Collectors.toMap(s -> s.substring(0, s.indexOf("=")), s -> s.substring(s.indexOf("=") + 1)));
		return Objects.equals(this.getName(), fields.getOrDefault("O", null)) && Objects.equals("" + this.getL(), fields.getOrDefault("L", null));
	}
	
	public int getL(){
		return l;
	}
	
	public int getA(){
		return a;
	}
	
	public int getB(){
		return b;
	}
	
	public String getID(){
		return ID;
	}
	
	public int getP(){
		return p;
	}
	
	public int getU(){
		return u;
	}
	
	public float getX(){
		return x;
	}
	
	public float getY(){
		return y;
	}
}
