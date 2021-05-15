package fr.raksrinana.rsndiscord.command.impl.twitter;

import fr.raksrinana.rsndiscord.api.twitter.TwitterApi;
import fr.raksrinana.rsndiscord.command.BasicCommand;
import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.permission.CreatorPermission;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.schedule.ScheduleUtils;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.command.CommandResult.BAD_ARGUMENTS;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

public class BlockCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	BlockCommand(Command parent){
		super(parent);
	}
	
	@NotNull
	@Override
	public CommandResult execute(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
		super.execute(event, args);
		if(args.isEmpty()){
			return BAD_ARGUMENTS;
		}
		args.forEach(user -> {
			var result = TwitterApi.blockUser(args.poll());
			JDAWrappers.message(event, "%s: %s".formatted(user, Boolean.toString(result.getData().isBlocking()))).submit()
					.thenAccept(ScheduleUtils.deleteMessage(date -> date.plusMinutes(1)));
		});
		return SUCCESS;
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return new CreatorPermission();
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return translate(guild, "command.twitter.block.name");
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return translate(guild, "command.twitter.block.description");
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("block", "b");
	}
	
	@Override
	public @NotNull String getCommandUsage(){
		return super.getCommandUsage() + " <username>";
	}
	
	@Override
	public void addHelp(@NotNull Guild guild, @NotNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("Username", translate(guild, "command.twitter.block.help.username"), false);
	}
}
