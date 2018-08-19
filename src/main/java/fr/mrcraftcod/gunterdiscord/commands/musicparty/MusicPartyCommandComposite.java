package fr.mrcraftcod.gunterdiscord.commands.musicparty;

import fr.mrcraftcod.gunterdiscord.commands.generic.CommandComposite;
import net.dv8tion.jda.core.entities.ChannelType;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 21/06/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-06-21
 */
public class MusicPartyCommandComposite extends CommandComposite{
	/**
	 * Constructor.
	 */
	public MusicPartyCommandComposite(){
		super();
		addSubCommand(new MusicPartyStartCommand(this));
		addSubCommand(new MusicPartyStopCommand(this));
		addSubCommand(new MusicPartyScoreCommand(this));
		addSubCommand(new MusicPartyMusicCommand(this));
		addSubCommand(new MusicPartySkipCommand(this));
	}
	
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@Override
	public String getName(){
		return "Fete de la musique";
	}
	
	@Override
	public List<String> getCommand(){
		return List.of("mp");
	}
	
	@Override
	public String getDescription(){
		return "Commandes pour le jeu de la fete de la musique";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
