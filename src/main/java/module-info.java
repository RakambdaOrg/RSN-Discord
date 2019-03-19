/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-05-03
 */
module fr.mrcraftcod.gunterdiscord {
	requires java.desktop;
	
	requires org.json;
	requires org.apache.commons.io;
	requires org.apache.commons.lang3;
	requires org.jetbrains.annotations;
	requires fr.mrcraftcod.utils.http;
	
	requires org.slf4j;
	//noinspection Java9RedundantRequiresStatement
	requires org.apache.logging.log4j;
	//noinspection Java9RedundantRequiresStatement
	requires org.apache.logging.log4j.slf4j;
	//noinspection Java9RedundantRequiresStatement
	requires java.scripting;
	
	requires net.dv8tion.jda;
	requires lavaplayer;
}