package fr.mrcraftcod.gunterdiscord.commands.luxbus.utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
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
		return admin;
	}
	
	public String getCategoryCode(){
		return categoryCode;
	}
	
	public String getLine(){
		return line;
	}
	
	public String getName(){
		return name;
	}
	
	public String getNumber(){
		return number;
	}
	
	public String getOperator(){
		return operator;
	}
	
	public String getOperatorCode(){
		return operatorCode;
	}
}
