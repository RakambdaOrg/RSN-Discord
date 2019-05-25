package fr.mrcraftcod.gunterdiscord.commands.luxbus.utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

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
	
	public String getAdmin(){
		return this.admin;
	}
	
	public String getCategoryCode(){
		return this.categoryCode;
	}
	
	public String getLine(){
		return this.line;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getNumber(){
		return this.number;
	}
	
	public String getOperator(){
		return this.operator;
	}
	
	public String getOperatorCode(){
		return this.operatorCode;
	}
}
