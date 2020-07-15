package fr.raksrinana.rsndiscord.commands;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.Utilities;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@BotCommand
public class AvatarCommand extends BasicCommand{
	@Override
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("User", translate(guild, "command.avatar.help.user"), false);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		if(event.getMessage().getMentionedUsers().isEmpty()){
			return CommandResult.BAD_ARGUMENTS;
		}
		final var user = event.getMessage().getMentionedUsers().get(0);
		final var builder = Utilities.buildEmbed(event.getAuthor(), Color.GREEN, translate(event.getGuild(), "avatar.title"), user.getAvatarUrl());
		builder.addField(translate(event.getGuild(), "avatar.link"), user.getAvatarUrl(), true);
		builder.setImage(user.getAvatarUrl());
		Actions.sendEmbed(event.getChannel(), builder.build());
		return CommandResult.SUCCESS;
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
