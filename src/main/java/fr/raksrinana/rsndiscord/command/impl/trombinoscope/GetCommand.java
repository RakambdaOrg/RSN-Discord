package fr.raksrinana.rsndiscord.command.impl.trombinoscope;

import fr.raksrinana.rsndiscord.command.BasicCommand;
import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.trombinoscope.Picture;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.internal.entities.UserById;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.NotNull;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import static fr.raksrinana.rsndiscord.command.CommandResult.BAD_ARGUMENTS;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.schedule.ScheduleUtils.deleteMessage;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.util.Optional.ofNullable;

class GetCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	GetCommand(Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NotNull Guild guild, @NotNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("user", translate(guild, "command.trombinoscope.get.help.user"), true)
				.addField("index", translate(guild, "command.trombinoscope.get.help.index"), true);
	}
	
	@NotNull
	@Override
	public CommandResult execute(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
		super.execute(event, args);
		var guild = event.getGuild();
		var channel = event.getChannel();
		
		var target = event.getMessage().getMentionedUsers().stream()
				.findFirst().stream()
				.peek(user -> args.poll())
				.findFirst()
				.or(() -> {
					try{
						return ofNullable(args.poll())
								.map(NumberUtils::createLong)
								.map(UserById::new);
					}
					catch(Exception e){
						return Optional.empty();
					}
				});
		if(target.isEmpty()){
			return BAD_ARGUMENTS;
		}
		
		var trombinoscope = Settings.get(guild).getTrombinoscope();
		var pictureCount = trombinoscope.getPictures(target.get()).size();
		if(pictureCount < 1){
			channel.sendMessage(translate(guild, "trombinoscope.user.no-picture")).submit();
			return SUCCESS;
		}
		
		var pictureIndex = getArgumentAsInteger(args)
				.filter(arg -> arg > 0 && arg <= pictureCount)
				.map(arg -> arg - 1)
				.orElseGet(() -> ThreadLocalRandom.current().nextInt(pictureCount));
		trombinoscope.getPictures(target.get()).stream()
				.sorted(Comparator.comparing(Picture::getDate))
				.skip(pictureIndex)
				.findFirst()
				.ifPresentOrElse(picture -> trombinoscope.getPicturesChannel()
								.flatMap(ChannelConfiguration::getChannel)
								.ifPresent(picturesChannel -> {
									var message = translate(guild, "trombinoscope.user.picture",
											event.getAuthor().getAsMention(),
											target.get().getAsMention(),
											picture.getUuid());
									picturesChannel.sendMessage(message)
											.addFile(picture.getPath().toFile())
											.submit();
								}),
						() -> channel.sendMessage(translate(guild, "trombinoscope.error.unknown")).submit()
								.thenAccept(deleteMessage(date -> date.plusMinutes(5))));
		return SUCCESS;
	}
	
	@Override
	public @NotNull String getCommandUsage(){
		return super.getCommandUsage() + " <user> [index]";
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return new SimplePermission("command.trombinoscope.get", true);
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return translate(guild, "command.trombinoscope.get.name");
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return translate(guild, "command.trombinoscope.get.description");
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("get", "g");
	}
}
