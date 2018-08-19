package fr.mrcraftcod.gunterdiscord.settings;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-04-15
 */
public class NoValueDefinedException extends Exception{
	private static final long serialVersionUID = 5747521705244357343L;
	
	/**
	 * Constructor.
	 *
	 * @param configuration The configuration concerned.
	 */
	public NoValueDefinedException(final Configuration configuration){
		super("The value of " + configuration.getName() + " insn't set.");
	}
}
