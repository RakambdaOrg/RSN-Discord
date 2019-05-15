package fr.mrcraftcod.gunterdiscord.commands.luxbus;

import java.util.Objects;

public class LuxBusStopID{
	private final String ID;
	
	public LuxBusStopID(final String id){ID = id.replace(";", "");}
	
	@Override
	public int hashCode(){
		return Objects.hash(ID);
	}
	
	@Override
	public boolean equals(final Object o){
		if(this == o){
			return true;
		}
		if(o == null || getClass() != o.getClass()){
			return false;
		}
		LuxBusStopID that = (LuxBusStopID) o;
		return Objects.equals(ID, that.ID);
	}
	
	@Override
	public String toString(){
		return this.getID();
	}
	
	public String getID(){
		return ID;
	}
}
