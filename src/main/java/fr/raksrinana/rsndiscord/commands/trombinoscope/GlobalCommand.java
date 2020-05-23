package fr.raksrinana.rsndiscord.commands.trombinoscope;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.trombinoscope.Picture;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.log.Log;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
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
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import static fr.raksrinana.rsndiscord.utils.TrombinoscopeUtils.isRegistered;

class GlobalCommand extends BasicCommand{
	private static final int PICTURE_PIXELS = 2000;
	
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	GlobalCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NonNull Guild guild, @NonNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("Mode", "Determines how the picture is created: [STRETCH (default), KEEP_ASPECT_RATIO]", false);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		var mode = Optional.ofNullable(args.poll()).flatMap(PictureMode::fromString).orElse(PictureMode.STRETCH);
		var trombinoscopeConfiguration = Settings.get(event.getGuild()).getTrombinoscope();
		var userCount = trombinoscopeConfiguration.getUserCount();
		var squareCountPerSide = (int) Math.ceil(Math.sqrt(userCount));
		var squarePixels = PICTURE_PIXELS / squareCountPerSide;
		var bufferedImage = new BufferedImage(squarePixels * squareCountPerSide, squarePixels * squareCountPerSide, BufferedImage.TYPE_INT_RGB);
		var g2d = bufferedImage.createGraphics();
		var currentIndex = new AtomicInteger(0);
		trombinoscopeConfiguration.getPictures().entrySet().stream().parallel().forEach(entry -> {
			var userIndex = currentIndex.getAndIncrement();
			entry.getValue().stream().skip(ThreadLocalRandom.current().nextInt(entry.getValue().size())).findFirst().ifPresent(picture -> {
				int x = userIndex / squareCountPerSide;
				int y = userIndex % squareCountPerSide;
				drawImage(g2d, picture, squarePixels * x, squarePixels * y, squarePixels, mode);
			});
		});
		g2d.dispose();
		ByteArrayOutputStream imgOutputStream = new ByteArrayOutputStream();
		try{
			if(ImageIO.write(bufferedImage, "jpeg", imgOutputStream)){
				try(var imgInputStream = new ByteArrayInputStream(imgOutputStream.toByteArray())){
					trombinoscopeConfiguration.getPicturesChannel().flatMap(ChannelConfiguration::getChannel).ifPresent(channel -> Actions.sendMessage(channel, "Full trombinoscope with " + userCount + " users. (Requested by " + event.getAuthor().getAsMention() + ")", null, false, action -> action.addFile(imgInputStream, System.currentTimeMillis() + ".jpg")));
				}
			}
		}
		catch(IOException e){
			Actions.reply(event, "Failed to create picture", null);
		}
		return CommandResult.SUCCESS;
	}
	
	@Override
	public @NonNull String getCommandUsage(){
		return super.getCommandUsage() + " [mode]";
	}
	
	@Override
	public boolean isAllowed(Member member){
		if(super.isAllowed(member)){
			return isRegistered(member);
		}
		return false;
	}
	
	private void drawImage(Graphics2D g2d, Picture picture, int x, int y, int dim, PictureMode mode){
		try{
			var image = ImageIO.read(picture.getPath().toFile());
			image = Scalr.resize(image, dim);
			switch(mode){
				case STRETCH -> g2d.drawImage(image, x, y, dim, dim, null);
				case KEEP_ASPECT_RATIO -> g2d.drawImage(image, x + (dim - image.getWidth()) / 2, y + (dim - image.getHeight()) / 2, image.getWidth(), image.getHeight(), null);
			}
		}
		catch(IOException e){
			Log.getLogger(null).error("Failed to read trombinoscope picture {}", picture.getPath(), e);
		}
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Wall";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("global", "gl");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Creates a wall of all the users of the trombinoscope";
	}
}
