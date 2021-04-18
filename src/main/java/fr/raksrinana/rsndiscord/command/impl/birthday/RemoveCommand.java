package fr.raksrinana.rsndiscord.command.impl.birthday;

import fr.raksrinana.rsndiscord.command.BasicCommand;
import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import fr.raksrinana.rsndiscord.settings.Settings;
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

public class RemoveCommand extends BasicCommand{
	public RemoveCommand(Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NotNull Guild guild, @NotNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("user", translate(guild, "command.birthday.remove.help.user"), false);
	}
	
	@NotNull
	@Override
	public CommandResult execute(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
		super.execute(event, args);
		
		if(noUserIsMentioned(event)){
			return BAD_ARGUMENTS;
		}
		
		args.poll();
		var guild = event.getGuild();
		var user = getFirstUserMentioned(event).orElseThrow();
		
		Settings.get(guild).getBirthdays().removeBirthday(user);
		JDAWrappers.message(event, translate(guild, "birthday.removed")).submit();
		return SUCCESS;
	}
	
	@Override
	public @NotNull String getCommandUsage(){
		return super.getCommandUsage() + " <@user>";
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return new SimplePermission("command.birthday.remove", false);
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return translate(guild, "command.birthday.remove.name");
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return translate(guild, "command.birthday.remove.description");
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("remove");
	}
}
