package fr.raksrinana.rsndiscord.commands;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.permission.Permission;
import fr.raksrinana.rsndiscord.utils.permission.SimplePermission;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@BotCommand
public class SoftBanCommand extends BasicCommand{
	@Override
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("user", translate(guild, "command.softban.help.user"), false);
		builder.addField("duration", translate(guild, "command.softban.help.duration"), false);
		builder.addField("reason", translate(guild, "command.softban.help.reason"), false);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		if(args.isEmpty()){
			return CommandResult.BAD_ARGUMENTS;
		}
		args.poll();
		var duration = Utilities.parseDuration(args.pop());
		var reason = String.join(" ", args);
		event.getMessage().getMentionedMembers().stream().findFirst()
				.ifPresent(member -> Actions.softBan(event.getChannel(), event.getAuthor(), member, reason, duration)
						.thenAccept(empty -> Actions.reply(event, translate(event.getGuild(), "softban.banned", member.getAsMention(), Utilities.durationToString(duration), reason), null)));
		return CommandResult.SUCCESS;
	}
	
	@NonNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <@user> <duration> <reason...>";
	}
	
	@Override
	public @NonNull Permission getPermission(){
		return new SimplePermission("command.softban", false);
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.softban.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("softban");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.softban.description");
	}
}
