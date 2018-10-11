package fr.mrcraftcod.gunterdiscord.utils.anilist;

import org.json.JSONObject;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-11.
 *
 * @author Thomas Couchoud
 * @since 2018-10-11
 */
public interface JSONFiller{
	void fromJSON(final JSONObject json) throws Exception;
}
