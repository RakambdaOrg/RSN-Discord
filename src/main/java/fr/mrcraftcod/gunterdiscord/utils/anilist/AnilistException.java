package fr.mrcraftcod.gunterdiscord.utils.anilist;

public class AnilistException extends Exception{
	public AnilistException(int status, String message){
		super(String.format("HTTP error %d with message \"%s\"", status, message));
	}
}
