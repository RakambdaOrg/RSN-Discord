package fr.mrcraftcod.gunterdiscord.utils.luxbus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.Nonnull;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LuxBusProduct{
	@JsonProperty("line")
	private String line;
	@JsonProperty("catCode")
	private String categoryCode;
	@JsonProperty("num")
	private String number;
	@JsonProperty("admin")
	private String admin;
	@JsonProperty("operator")
	private String operator;
	@JsonProperty("operatorCode")
	private String operatorCode;
	@JsonProperty("name")
	private String name;
	
	@Nonnull
	public String getAdmin(){
		return this.admin;
	}
	
	@Nonnull
	public String getCategoryCode(){
		return this.categoryCode;
	}
	
	@Nonnull
	public String getLine(){
		return this.line;
	}
	
	@Nonnull
	public String getName(){
		return this.name;
	}
	
	@Nonnull
	public String getNumber(){
		return this.number;
	}
	
	@Nonnull
	public String getOperator(){
		return this.operator;
	}
	
	@Nonnull
	public String getOperatorCode(){
		return this.operatorCode;
	}
}
