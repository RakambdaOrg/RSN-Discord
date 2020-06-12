package fr.raksrinana.rsndiscord.commands.trombinoscope;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.trombinoscope.Picture;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static fr.raksrinana.rsndiscord.utils.TrombinoscopeUtils.isRegistered;

class GetCommand extends BasicCommand{
	private static final Path trombinoscopeFolder = Paths.get("trombinoscope");
	
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	GetCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public boolean isAllowed(Member member){
		if(super.isAllowed(member)){
			return isRegistered(member);
		}
		return false;
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.trombinoscope.get.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("get", "g");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.trombinoscope.get.description");
	}
	
	@Override
	public void addHelp(@NonNull Guild guild, @NonNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("user", translate(guild, "command.trombinoscope.get.help.user"), true);
		builder.addField("index", translate(guild, "command.trombinoscope.get.help.index"), true);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		if(event.getMessage().getMentionedUsers().isEmpty()){
			return CommandResult.BAD_ARGUMENTS;
		}
		args.pop();
		var trombinoscope = Settings.get(event.getGuild()).getTrombinoscope();
		var target = event.getMessage().getMentionedUsers().get(0);
		var pictureCount = trombinoscope.getPictures(target).size();
		if(pictureCount < 1){
			Actions.reply(event, translate(event.getGuild(), "trombinoscope.user.no-picture"), null);
			return CommandResult.SUCCESS;
		}
		var pictureIndex = Optional.ofNullable(args.poll())
				.map(arg -> {
					try{
						return Integer.parseInt(arg);
					}
					catch(NumberFormatException ignored){
					}
					return null;
				}).filter(arg -> arg > 0 && arg <= pictureCount)
				.map(arg -> arg - 1)
				.orElseGet(() -> ThreadLocalRandom.current().nextInt(pictureCount));
		trombinoscope.getPictures(target).stream()
				.sorted(Comparator.comparing(Picture::getDate))
				.skip(pictureIndex)
				.findFirst()
				.ifPresentOrElse(picture -> trombinoscope.getPicturesChannel()
						.flatMap(ChannelConfiguration::getChannel)
						.ifPresent(picturesChannel -> Actions.sendMessage(picturesChannel, translate(event.getGuild(), "trombinoscope.user.picture", event.getAuthor().getAsMention(), target.getAsMention(), picture.getUuid()), null, false, message -> message.addFile(picture.getPath().toFile()))), () -> Actions.reply(event, translate(event.getGuild(), "trombinoscope.error.unknown"), null));
		return CommandResult.SUCCESS;
	}
	
	@Override
	public @NonNull String getCommandUsage(){
		return super.getCommandUsage() + " <user> [index]";
	}
}
