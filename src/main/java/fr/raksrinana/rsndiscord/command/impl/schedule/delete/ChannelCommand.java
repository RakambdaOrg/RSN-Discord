package fr.raksrinana.rsndiscord.command.impl.schedule.delete;

import fr.raksrinana.rsndiscord.command.BasicCommand;
import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import fr.raksrinana.rsndiscord.schedule.ScheduleUtils;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.reaction.WaitingReactionMessageConfiguration;
import fr.raksrinana.rsndiscord.settings.guild.schedule.SimpleScheduleConfiguration;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import static fr.raksrinana.rsndiscord.command.CommandResult.BAD_ARGUMENTS;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.command.impl.schedule.ScheduleCommandComposite.getReminderDate;
import static fr.raksrinana.rsndiscord.reaction.ReactionTag.SCHEDULED_DELETE_CHANNEL;
import static fr.raksrinana.rsndiscord.schedule.ScheduleTag.DELETE_CHANNEL;
import static fr.raksrinana.rsndiscord.utils.BasicEmotes.CROSS_NO;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

public class ChannelCommand extends BasicCommand{
	public ChannelCommand(Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NotNull Guild guild, @NotNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("channel", translate(guild, "command.schedule.delete.channel.help.channel"), false);
	}
	
	@NotNull
	@Override
	public CommandResult execute(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
		super.execute(event, args);
		if(args.isEmpty()){
			return BAD_ARGUMENTS;
		}
		return getReminderDate(args.pop()).map(date -> {
			var toDelete = event.getMessage().getMentionedChannels().stream().findFirst().orElse(event.getChannel());
			scheduleDeletion(date, toDelete, event.getAuthor());
			return SUCCESS;
		}).orElse(BAD_ARGUMENTS);
	}
	
	public static void scheduleDeletion(@NotNull ZonedDateTime date, @NotNull TextChannel channel, @NotNull User author){
		var scheduleConfiguration = new SimpleScheduleConfiguration(author, channel, date,
				translate(channel.getGuild(), "schedule.delete.channel.message"), DELETE_CHANNEL);
		Consumer<EmbedBuilder> builderConsumer = builder -> builder.addField(translate(channel.getGuild(), "schedule.info"),
				translate(channel.getGuild(), "schedule.react-to-cancel", CROSS_NO.getValue()), false);
		ScheduleUtils.addScheduleAndNotify(scheduleConfiguration, channel, builderConsumer).thenAccept(message -> {
			message.addReaction(CROSS_NO.getValue()).submit();
			Settings.get(channel.getGuild()).addMessagesAwaitingReaction(new WaitingReactionMessageConfiguration(message, SCHEDULED_DELETE_CHANNEL));
		});
	}
	
	@Override
	public @NotNull String getCommandUsage(){
		return super.getCommandUsage() + " <delay> [channel]";
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return new SimplePermission("command.schedule.delete.channel", false);
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return translate(guild, "command.schedule.delete.channel.name");
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return translate(guild, "command.schedule.delete.channel.description");
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("c", "channel");
	}
}
