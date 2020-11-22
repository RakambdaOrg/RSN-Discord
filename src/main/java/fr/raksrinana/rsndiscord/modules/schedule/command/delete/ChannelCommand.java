package fr.raksrinana.rsndiscord.modules.schedule.command.delete;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.modules.permission.IPermission;
import fr.raksrinana.rsndiscord.modules.permission.SimplePermission;
import fr.raksrinana.rsndiscord.modules.reaction.config.WaitingReactionMessageConfiguration;
import fr.raksrinana.rsndiscord.modules.schedule.ScheduleUtils;
import fr.raksrinana.rsndiscord.modules.schedule.config.SimpleScheduleConfiguration;
import fr.raksrinana.rsndiscord.modules.settings.Settings;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import static fr.raksrinana.rsndiscord.commands.generic.CommandResult.BAD_ARGUMENTS;
import static fr.raksrinana.rsndiscord.commands.generic.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.modules.reaction.ReactionTag.SCHEDULED_DELETE_CHANNEL;
import static fr.raksrinana.rsndiscord.modules.schedule.ScheduleTag.DELETE_CHANNEL;
import static fr.raksrinana.rsndiscord.modules.schedule.command.ScheduleCommandComposite.getReminderDate;
import static fr.raksrinana.rsndiscord.utils.BasicEmotes.CROSS_NO;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

public class ChannelCommand extends BasicCommand{
	public ChannelCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NonNull Guild guild, @NonNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("channel", translate(guild, "command.schedule.delete.channel.help.channel"), false);
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return new SimplePermission("command.schedule.delete.channel", false);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
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
	
	public static void scheduleDeletion(ZonedDateTime date, TextChannel channel, User author){
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
	public @NonNull String getCommandUsage(){
		return super.getCommandUsage() + " <delay> [channel]";
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.schedule.delete.channel.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("c", "channel");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.schedule.delete.channel.description");
	}
}
