package fr.raksrinana.rsndiscord.utils;

import org.jetbrains.annotations.NotNull;

public enum BasicEmotes implements Comparable<BasicEmotes>{
	A("\uD83C\uDDE6", "a", "A"),
	B("\uD83C\uDDE7", "b", "B"),
	C("\uD83C\uDDE8", "c", "C"),
	CHECK_OK("✅"),
	CROSS_NO("❌"),
	D("\uD83C\uDDE9", "d", "D"),
	E("\uD83C\uDDEA", "e", "E"),
	ERROR(""),
	F("\uD83C\uDDEB", "f", "F"),
	G("\uD83C\uDDEC", "g", "G"),
	H("\uD83C\uDDED", "h", "H"),
	I("\uD83C\uDDEE", "i", "I"),
	J("\uD83C\uDDEF", "j", "J"),
	K("\uD83C\uDDF0", "k", "K"),
	L("\uD83C\uDDF1", "l", "L"),
	M("\uD83C\uDDF2", "m", "M"),
	N("\uD83C\uDDF3", "n", "N"),
	O("\uD83C\uDDF4", "o", "O"),
	OK_HAND("👌"),
	P("\uD83C\uDDF5", "p", "P"),
	PACKAGE("📦", "\uD83D\uDCE6"),
	PAPERCLIP("\uD83D\uDCCE"),
	Q("\uD83C\uDDF6", "q", "Q"),
	R("\uD83C\uDDF7", "r", "R"),
	RIGHT_ARROW_CURVING_LEFT("↩️"),
	S("\uD83C\uDDF8", "s", "S"),
	T("\uD83C\uDDF9", "t", "T"),
	THUMB_DOWN("\uD83D\uDC4E"),
	THUMB_UP("\uD83D\uDC4D"),
	U("\uD83C\uDDFA", "u", "U"),
	V("\uD83C\uDDFB", "v", "V"),
	W("\uD83C\uDDFC", "w", "W"),
	X("\uD83C\uDDFD", "x", "X"),
	Y("\uD83C\uDDFE", "y", "Y"),
	Z("\uD83C\uDDFF", "z", "Z");
	private final String name;
	private final String[] others;
	
	/**
	 * Constructor.
	 *
	 * @param name The representation of the emote.
	 */
	BasicEmotes(@NotNull String name){
		this.name = name;
		others = new String[]{};
	}
	
	/**
	 * Constructor.
	 *
	 * @param name   The representation of the emote.
	 * @param others The other representations of the emote.
	 */
	BasicEmotes(@NotNull String name, @NotNull String... others){
		this.name = name;
		this.others = others;
	}
	
	/**
	 * Get an emote by a representation.
	 *
	 * @param text The representation.
	 *
	 * @return The emote, or null if non were found.
	 */
	@NotNull
	public static BasicEmotes getEmote(@NotNull String text){
		for(var emote : BasicEmotes.values()){
			if(emote.getValue().equals(text)){
				return emote;
			}
			for(var other : emote.getOthers()){
				if(other.equals(text)){
					return emote;
				}
			}
		}
		return ERROR;
	}
	
	/**
	 * Get the value of this emote.
	 *
	 * @return The emote's value.
	 */
	@NotNull
	public String getValue(){
		return name;
	}
	
	/**
	 * Get the other representations.
	 *
	 * @return The other representations.
	 */
	@NotNull
	private String[] getOthers(){
		return others;
	}
}

