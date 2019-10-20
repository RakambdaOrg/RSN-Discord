package fr.raksrinana.rsndiscord.commands.generic;

import javax.annotation.Nonnull;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-05-07
 */
public class NotAllowedException extends Exception{
	private static final long serialVersionUID = -647603631725613643L;
	
	/**
	 * Constructor.
	 */
	public NotAllowedException(@Nonnull final String message){
		super(message);
	}
}
