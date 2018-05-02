package fr.mrcraftcod.gunterdiscord.utils;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-05-02
 */
public enum Roles
{
	TROMBINOSCOPE("Trombi"),
	HANGMAN("pendu");
	
	private final String role;
	
	Roles(String role)
	{
		this.role = role;
	}
	
	public String getRole()
	{
		return role;
	}
}
