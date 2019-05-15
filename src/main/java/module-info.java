/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-05-03
 */
open module fr.mrcraftcod.gunterdiscord {
	requires java.desktop;
	
	requires org.json;
	requires org.apache.commons.io;
	requires org.apache.commons.lang3;
	requires org.jetbrains.annotations;
	requires fr.mrcraftcod.utils.http;
	requires jcommander;
	
	requires org.slf4j;
	requires ch.qos.logback.classic;
	
	requires net.dv8tion.jda;
	requires lavaplayer;
}