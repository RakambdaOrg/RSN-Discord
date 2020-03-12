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
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;

public class ChannelCommand extends BasicCommand{
	public ChannelCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NonNull Guild guild, @NonNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("channel", "The channel to delete. If empty, the current channel will be the target.", false);
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
			ScheduleUtils.addScheduleAndNotify(new SimpleScheduleConfiguration(event.getAuthor(), toDelete, date, "Deleting this channel", ScheduleTag.DELETE_CHANNEL), toDelete, builder -> builder.addField("Info", "React " + BasicEmotes.CROSS_NO.getValue() + " to cancel the deletion", false)).thenAccept(message -> {
				Actions.addReaction(message, BasicEmotes.CROSS_NO.getValue());
				Settings.get(event.getGuild()).addMessagesAwaitingReaction(new WaitingReactionMessageConfiguration(message, ReactionTag.SCHEDULED_DELETE_CHANNEL));
			});
			return CommandResult.SUCCESS;
		}).orElse(CommandResult.BAD_ARGUMENTS);
	}
	
	@Override
	public @NonNull String getCommandUsage(){
		return super.getCommandUsage() + " <delay> [channel]";
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Delete channel";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("c", "channel");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Schedule the deletion of a channel";
	}
}
