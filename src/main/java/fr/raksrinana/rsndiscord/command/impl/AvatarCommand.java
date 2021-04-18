package fr.raksrinana.rsndiscord.command.impl;

import fr.raksrinana.rsndiscord.command.BasicCommand;
import fr.raksrinana.rsndiscord.command.BotCommand;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
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
import static java.awt.Color.GREEN;

@BotCommand
public class AvatarCommand extends BasicCommand{
	@Override
	public void addHelp(@NotNull Guild guild, @NotNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("User", translate(guild, "command.avatar.help.user"), false);
	}
	
	@NotNull
	@Override
	public CommandResult execute(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
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
		JDAWrappers.message(event, embed).submit();
		return SUCCESS;
	}
	
	@NotNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <@user>";
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return new SimplePermission("command.avatar", true);
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return translate(guild, "command.avatar.name");
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return translate(guild, "command.avatar.description");
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("avatar");
	}
}
