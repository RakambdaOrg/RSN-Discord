package fr.raksrinana.rsndiscord.modules.stopwatch.command;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.modules.permission.IPermission;
import fr.raksrinana.rsndiscord.modules.permission.SimplePermission;
import fr.raksrinana.rsndiscord.modules.stopwatch.reply.StopwatchReply;
import fr.raksrinana.rsndiscord.reply.UserReplyEventListener;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.commands.generic.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.utils.BasicEmotes.*;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.awt.Color.GREEN;

@BotCommand
public class StopwatchCommand extends BasicCommand{
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		var embed = buildEmbed(event.getGuild(), event.getAuthor(), "");
		event.getChannel().sendMessage(embed).submit()
				.thenAccept(message -> {
					message.addReaction(P.getValue()).submit();
					message.addReaction(S.getValue()).submit();
					UserReplyEventListener.handleReply(new StopwatchReply(event, message));
				});
		return SUCCESS;
	}
	
	@NonNull
	public static MessageEmbed buildEmbed(@NonNull Guild guild, @NonNull User user, @NonNull String time){
		return new EmbedBuilder().setAuthor(user.getName(), null, user.getAvatarUrl())
				.setColor(GREEN)
				.setTitle(translate(guild, "stopwatch.name"))
				.addField(translate(guild, "stopwatch.time"), time, false)
				.addField(P.getValue(), translate(guild, "stopwatch.pause"), true)
				.addField(R.getValue(), translate(guild, "stopwatch.resume"), true)
				.addField(S.getValue(), translate(guild, "stopwatch.stop"), true)
				.build();
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return new SimplePermission("command.stopwatch", false);
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.stopwatch.name");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.stopwatch.description");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("stopwatch");
	}
}
