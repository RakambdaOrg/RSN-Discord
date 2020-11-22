package fr.raksrinana.rsndiscord.commands;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.modules.permission.IPermission;
import fr.raksrinana.rsndiscord.modules.permission.SimplePermission;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.commands.generic.CommandResult.BAD_ARGUMENTS;
import static fr.raksrinana.rsndiscord.commands.generic.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.awt.Color.GREEN;

@BotCommand
public class AvatarCommand extends BasicCommand{
	@Override
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("User", translate(guild, "command.avatar.help.user"), false);
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return new SimplePermission("command.avatar", true);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		if(noUserIsMentioned(event)){
			return BAD_ARGUMENTS;
		}
		
		var guild = event.getGuild();
		var author = event.getAuthor();
		
		var target = getFirstUserMentioned(event).orElseThrow();
		
		var embed = new EmbedBuilder().setAuthor(author.getName(), null, author.getAvatarUrl())
				.setColor(GREEN)
				.setTitle(translate(guild, "avatar.title"))
				.addField(translate(guild, "avatar.link"), target.getAvatarUrl(), true)
				.setImage(target.getAvatarUrl())
				.build();
		event.getChannel().sendMessage(embed).submit();
		return SUCCESS;
	}
	
	@NonNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <@user>";
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.avatar.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("avatar");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.avatar.description");
	}
}
