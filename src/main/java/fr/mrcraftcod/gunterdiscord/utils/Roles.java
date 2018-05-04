package fr.mrcraftcod.gunterdiscord.utils;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-05-02
 */
public enum Roles
{
	TROMBINOSCOPE("Trombi"), HANGMAN("pendu"), MODERATOR("Kaporal (modo)");
	
	private final String role;
	
	/**
	 * Constructor.
	 *
	 * @param role The role's name.
	 */
	Roles(String role)
	{
		this.role = role;
	}
	
	/**
	 * Get the role's name.
	 *
	 * @return Its name.
	 */
	public String getRole()
	{
		return role;
	}
}
