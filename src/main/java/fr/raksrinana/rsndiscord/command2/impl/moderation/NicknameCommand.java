package fr.raksrinana.rsndiscord.command2.impl.moderation;

import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.command2.base.group.SubCommand;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.awt.Color.*;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;
import static net.dv8tion.jda.api.interactions.commands.OptionType.USER;

public class NicknameCommand extends SubCommand{
	private static final String USER_OPTION_ID = "user";
	private static final String NICK_OPTION_ID = "nick";
	
	@Override
	@NotNull
	public String getId(){
		return "nickname";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Change the nickname of a user";
	}
	
	@Override
	@NotNull
	protected Collection<? extends OptionData> getOptions(){
		return List.of(
				new OptionData(USER, USER_OPTION_ID, "The user to change the nickname").setRequired(true),
				new OptionData(STRING, NICK_OPTION_ID, "The nickname to set (leave empty to reset it)"));
	}
	
	@Override
	public boolean replyEphemeral(){
		return false;
	}
	
	@Override
	@NotNull
	public CommandResult execute(@NotNull SlashCommandEvent event){
		var guild = event.getGuild();
		var author = event.getUser();
		
		var member = event.getOption(USER_OPTION_ID).getAsMember();
		var user = member.getUser();
		
		var oldNickname = Optional.ofNullable(member.getNickname());
		var newNickname = Optional.ofNullable(event.getOption(NICK_OPTION_ID))
				.map(OptionMapping::getAsString)
				.filter(val -> !val.isBlank());
		
		var builder = new EmbedBuilder()
				.setAuthor(author.getName(), null, author.getAvatarUrl())
				.addField(translate(guild, "nickname.user"), user.getAsMention(), true)
				.addField(translate(guild, "nickname.old-nick"), oldNickname.orElseGet(() -> translate(guild, "nickname.unknown")), true)
				.addField(translate(guild, "nickname.new-nick"), newNickname.orElseGet(() -> translate(guild, "nickname.unknown")), true)
				.setTimestamp(ZonedDateTime.now());
		
		if(newNickname.equals(oldNickname)){
			builder.setColor(ORANGE)
					.setTitle(translate(guild, "nickname.no-changes"))
					.addField(translate(guild, "nickname.reason"), translate(guild, "nickname.same-nickname"), false);
			JDAWrappers.replyCommand(event, builder.build()).submitAndDelete(10);
			return SUCCESS;
		}
		
		try{
			JDAWrappers.modifyNickname(member, newNickname.orElse(null)).submit()
					.thenAccept(empty -> {
						Log.getLogger(guild).info("{} renamed {} from `{}` to `{}`", author, member, oldNickname, newNickname);
						
						builder.setColor(GREEN);
						
						JDAWrappers.replyCommand(event, builder.build()).submit();
					})
					.exceptionally(error -> {
						var errorMessage = error instanceof ErrorResponseException ? ((ErrorResponseException) error).getMeaning() : error.getMessage();
						builder.setColor(RED)
								.setTitle(translate(guild, "nickname.invalid"))
								.addField(translate(guild, "nickname.reason"), errorMessage, false);
						JDAWrappers.replyCommand(event, builder.build()).submitAndDelete(10);
						return null;
					});
		}
		catch(HierarchyException e){
			builder.setColor(RED)
					.addField(translate(guild, "nickname.reason"), translate(guild, "nickname.target-error"), false);
			JDAWrappers.replyCommand(event, builder.build()).submitAndDelete(10);
		}
		return SUCCESS;
	}
}
