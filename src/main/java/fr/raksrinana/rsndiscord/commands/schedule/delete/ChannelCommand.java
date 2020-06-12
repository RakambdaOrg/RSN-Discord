package fr.raksrinana.rsndiscord.commands.schedule.delete;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.commands.schedule.ScheduleCommandComposite;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.WaitingReactionMessageConfiguration;
import fr.raksrinana.rsndiscord.settings.guild.schedule.SimpleScheduleConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import fr.raksrinana.rsndiscord.utils.reaction.ReactionTag;
import fr.raksrinana.rsndiscord.utils.schedule.ScheduleTag;
import fr.raksrinana.rsndiscord.utils.schedule.ScheduleUtils;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;

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
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		if(args.isEmpty()){
			return CommandResult.BAD_ARGUMENTS;
		}
		return ScheduleCommandComposite.getReminderDate(args.pop()).map(date -> {
			final var toDelete = event.getMessage().getMentionedChannels().stream().findFirst().orElse(event.getChannel());
			scheduleDeletion(date, toDelete, event.getAuthor());
			return CommandResult.SUCCESS;
		}).orElse(CommandResult.BAD_ARGUMENTS);
	}
	
	public static void scheduleDeletion(ZonedDateTime date, TextChannel channel, User author){
		ScheduleUtils.addScheduleAndNotify(new SimpleScheduleConfiguration(author, channel, date, translate(channel.getGuild(), "schedule.delete.channel.message"), ScheduleTag.DELETE_CHANNEL), channel, builder -> builder.addField(translate(channel.getGuild(), "schedule.info"), translate(channel.getGuild(), "schedule.react-to-cancel", BasicEmotes.CROSS_NO.getValue()), false)).thenAccept(message -> {
			Actions.addReaction(message, BasicEmotes.CROSS_NO.getValue());
			Settings.get(channel.getGuild()).addMessagesAwaitingReaction(new WaitingReactionMessageConfiguration(message, ReactionTag.SCHEDULED_DELETE_CHANNEL));
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
