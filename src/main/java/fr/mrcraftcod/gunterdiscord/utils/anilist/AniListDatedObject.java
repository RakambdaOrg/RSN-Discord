package fr.mrcraftcod.gunterdiscord.utils.anilist;

import javax.annotation.Nonnull;
import java.util.Date;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-11.
 *
 * @author Thomas Couchoud
 * @since 2018-10-11
 */
public interface AniListDatedObject extends AniListObject{
	@Nonnull
	Date getDate();
}
