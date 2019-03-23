package fr.mrcraftcod.gunterdiscord.utils.anilist;

class AnilistException extends Exception{
	private static final long serialVersionUID = 2679562906118469667L;
	
	AnilistException(int status, String message){
		super(String.format("HTTP error %d with message \"%s\"", status, message));
	}
}
