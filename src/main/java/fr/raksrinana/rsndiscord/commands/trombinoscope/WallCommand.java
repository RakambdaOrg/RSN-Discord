package fr.raksrinana.rsndiscord.commands.trombinoscope;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.trombinoscope.Picture;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.log.Log;
import lombok.NonNull;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.imgscalr.Scalr;
import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

class WallCommand extends BasicCommand{
	private static final int PICTURE_PIXELS = 2000;
	
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	WallCommand(final Command parent){
		super(parent);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		var trombinoscopeConfiguration = Settings.get(event.getGuild()).getTrombinoscope();
		var userCount = trombinoscopeConfiguration.getUserCount();
		var squareCountPerSide = (int) Math.ceil(Math.sqrt(userCount));
		var squarePixels = PICTURE_PIXELS / squareCountPerSide;
		var bufferedImage = new BufferedImage(squarePixels * squareCountPerSide, squarePixels * squareCountPerSide, BufferedImage.TYPE_INT_RGB);
		var g2d = bufferedImage.createGraphics();
		var currentIndex = new AtomicInteger(0);
		trombinoscopeConfiguration.getPictures().forEach((userId, pictures) -> {
			var userIndex = currentIndex.getAndIncrement();
			pictures.stream().skip(0).findFirst().ifPresent(picture -> {
				int x = userIndex / squareCountPerSide;
				int y = userIndex % squareCountPerSide;
				drawImage(g2d, picture, squarePixels * x, squarePixels * y, squarePixels);
			});
		});
		g2d.dispose();
		ByteArrayOutputStream imgOutputStream = new ByteArrayOutputStream();
		try{
			if(ImageIO.write(bufferedImage, "jpeg", imgOutputStream)){
				try(var imgInputStream = new ByteArrayInputStream(imgOutputStream.toByteArray())){
					// trombinoscopeConfiguration.getPicturesChannel().flatMap(ChannelConfiguration::getChannel).ifPresent(channel -> {
					Optional.ofNullable(event.getGuild().getTextChannelById(436492330602594304L)).ifPresent(channel -> {
						Actions.sendMessage(channel, "", null, false, action -> action.addFile(imgInputStream, System.currentTimeMillis() + ".jpg"));
					});
				}
			}
		}
		catch(IOException e){
			Actions.reply(event, "Failed to create picture", null);
		}
		return CommandResult.SUCCESS;
	}
	
	private void drawImage(Graphics2D g2d, Picture picture, int x, int y, int dim){
		try{
			var image = ImageIO.read(picture.getPath().toFile());
			image = Scalr.resize(image, dim);
			g2d.drawImage(image, x + (dim - image.getWidth()) / 2, y + (dim - image.getHeight()) / 2, image.getWidth(), image.getHeight(), null);
		}
		catch(IOException e){
			Log.getLogger(null).error("Failed to read trombinoscope picture {}", picture.getPath(), e);
		}
	}
	
	@Override
	public @NonNull AccessLevel getAccessLevel(){
		return AccessLevel.MODERATOR;
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Wall";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("wall", "w");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Creates a wall of all the users of the trombinoscope";
	}
}
