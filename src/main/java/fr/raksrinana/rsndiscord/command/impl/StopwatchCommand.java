package fr.raksrinana.rsndiscord.command.impl;

import fr.raksrinana.rsndiscord.command.BasicCommand;
import fr.raksrinana.rsndiscord.command.BotCommand;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import fr.raksrinana.rsndiscord.reply.StopwatchReply;
import fr.raksrinana.rsndiscord.reply.UserReplyEventListener;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.utils.BasicEmotes.*;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.awt.Color.GREEN;

@BotCommand
public class StopwatchCommand extends BasicCommand{
	@NotNull
	@Override
	public CommandResult execute(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
		super.execute(event, args);
		var embed = buildEmbed(event.getGuild(), event.getAuthor(), "");
		JDAWrappers.message(event, embed).submit()
				.thenAccept(message -> {
					message.addReaction(P.getValue()).submit();
					message.addReaction(S.getValue()).submit();
					UserReplyEventListener.handleReply(new StopwatchReply(event, message));
				});
		return SUCCESS;
	}
	
	@NotNull
	public static MessageEmbed buildEmbed(@NotNull Guild guild, @NotNull User user, @NotNull String time){
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
	public @NotNull IPermission getPermission(){
		return new SimplePermission("command.stopwatch", false);
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return translate(guild, "command.stopwatch.name");
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return translate(guild, "command.stopwatch.description");
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("stopwatch");
	}
}
