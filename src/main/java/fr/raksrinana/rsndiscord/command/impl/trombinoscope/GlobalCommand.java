package fr.raksrinana.rsndiscord.command.impl.trombinoscope;

import fr.raksrinana.rsndiscord.command.BasicCommand;
import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import fr.raksrinana.rsndiscord.schedule.ScheduleUtils;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.trombinoscope.Picture;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import net.coobird.thumbnailator.Thumbnails;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.imgscalr.Scalr;
import org.jetbrains.annotations.NotNull;
import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.command.impl.trombinoscope.PictureMode.KEEP_ASPECT_RATIO;
import static fr.raksrinana.rsndiscord.command.impl.trombinoscope.PictureMode.STRETCH;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.util.Comparator.comparingInt;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toSet;

class GlobalCommand extends BasicCommand{
	private static final int PICTURE_PIXELS = 500;
	
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	GlobalCommand(Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NotNull Guild guild, @NotNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("mode", translate(guild, "command.trombinoscope.global.help.mode"), false);
	}
	
	@NotNull
	@Override
	public CommandResult execute(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
		super.execute(event, args);
		var guild = event.getGuild();
		var mode = ofNullable(args.poll()).flatMap(PictureMode::fromString).orElse(STRETCH);
		var trombinoscopeConfiguration = Settings.get(guild).getTrombinoscope();
		var userCount = trombinoscopeConfiguration.getUserCount();
		var dimensions = getDims(userCount);
		
		var bufferedImage = new BufferedImage(PICTURE_PIXELS * dimensions.getLeft(), PICTURE_PIXELS * dimensions.getRight(), BufferedImage.TYPE_INT_RGB);
		var g2d = bufferedImage.createGraphics();
		
		var currentIndex = new AtomicInteger(0);
		trombinoscopeConfiguration.getPictures().entrySet().stream().parallel().forEach(entry -> {
			var userIndex = currentIndex.getAndIncrement();
			entry.getValue().stream().skip(ThreadLocalRandom.current().nextInt(entry.getValue().size())).findFirst()
					.ifPresent(picture -> {
						int x = userIndex % dimensions.getLeft();
						int y = userIndex / dimensions.getLeft();
						drawImage(g2d, picture, PICTURE_PIXELS * x, PICTURE_PIXELS * y, PICTURE_PIXELS, mode);
					});
		});
		g2d.dispose();
		
		ByteArrayOutputStream imgOutputStream = new ByteArrayOutputStream();
		try{
			if(ImageIO.write(bufferedImage, "jpeg", imgOutputStream)){
				try(var imgInputStream = new ByteArrayInputStream(imgOutputStream.toByteArray())){
					trombinoscopeConfiguration.getPicturesChannel()
							.flatMap(ChannelConfiguration::getChannel)
							.ifPresent(channel -> {
								var message = translate(guild, "trombinoscope.global", userCount, event.getAuthor().getAsMention());
								channel.sendMessage(message)
										.addFile(imgInputStream, System.currentTimeMillis() + ".jpg")
										.submit();
							});
				}
			}
		}
		catch(IOException e){
			event.getChannel().sendMessage(translate(guild, "trombinoscope.error.create-fail")).submit()
					.thenAccept(ScheduleUtils.deleteMessage(date -> date.plusMinutes(5)));
		}
		return SUCCESS;
	}
	
	@NotNull
	private static Pair<Integer, Integer> getDims(int count){
		var divisors = IntStream.rangeClosed(1, (int) Math.floor(Math.sqrt(count)))
				.filter(i -> count % i == 0)
				.mapToObj(i -> Pair.of(i, count / i))
				.collect(toSet());
		return divisors.stream()
				.min(comparingInt(GlobalCommand::getDimsDiff))
				.orElseGet(() -> {
					var side = (int) Math.ceil(Math.sqrt(count));
					return Pair.of(side, side);
				});
	}
	
	private void drawImage(@NotNull Graphics2D g2d, @NotNull Picture picture, int x, int y, int dim, @NotNull PictureMode mode){
		try{
			var image = Thumbnails.of(picture.getPath().toFile())
					.size(dim, dim)
					.keepAspectRatio(mode == KEEP_ASPECT_RATIO)
					.useExifOrientation(true)
					.asBufferedImage();
			image = Scalr.resize(image, dim);
			g2d.drawImage(image, x, y, null);
		}
		catch(IOException e){
			Log.getLogger(null).error("Failed to read trombinoscope picture {}", picture.getPath(), e);
		}
	}
	
	private static int getDimsDiff(@NotNull Pair<Integer, Integer> dimensions){
		return Math.abs(dimensions.getLeft() - dimensions.getRight());
	}
	
	@Override
	public @NotNull String getCommandUsage(){
		return super.getCommandUsage() + " [mode]";
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return new SimplePermission("command.trombinoscope.global", true);
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return translate(guild, "command.trombinoscope.global.name");
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return translate(guild, "command.trombinoscope.global.description");
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("global", "gl");
	}
}
