package fr.raksrinana.rsndiscord.interaction.command.slash.impl.moderation;

import fr.raksrinana.rsndiscord.interaction.command.CommandResult;
import fr.raksrinana.rsndiscord.interaction.command.slash.base.group.SubSlashCommand;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionException;
import static fr.raksrinana.rsndiscord.interaction.command.CommandResult.HANDLED;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.awt.Color.GREEN;
import static java.awt.Color.ORANGE;
import static java.awt.Color.RED;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;
import static net.dv8tion.jda.api.interactions.commands.OptionType.USER;

@Log4j2
public class NicknameCommand extends SubSlashCommand{
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
	public CommandResult executeGuild(@NotNull SlashCommandInteraction event, @NotNull Guild guild, @NotNull Member member){
		var author = event.getUser();
		
		var target = event.getOption(USER_OPTION_ID).getAsMember();
		var user = target.getUser();
		
		var oldNickname = Optional.ofNullable(target.getNickname());
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
			JDAWrappers.edit(event, builder.build()).submitAndDelete(10);
			return HANDLED;
		}
		
		try{
			JDAWrappers.modifyNickname(target, newNickname.orElse(null)).submit()
					.thenAccept(empty -> {
						log.info("{} renamed {} from `{}` to `{}`", author, target, oldNickname, newNickname);
						
						builder.setColor(GREEN);
						
						JDAWrappers.edit(event, builder.build()).submit();
					})
					.exceptionally(e -> {
						var errorMessage = handleException(e);
						builder.setColor(RED)
								.setTitle(translate(guild, "nickname.invalid"))
								.addField(translate(guild, "nickname.reason"), errorMessage, false);
						JDAWrappers.edit(event, builder.build()).submitAndDelete(10);
						return null;
					});
		}
		catch(HierarchyException e){
			builder.setColor(RED)
					.addField(translate(guild, "nickname.reason"), translate(guild, "nickname.target-error"), false);
			JDAWrappers.edit(event, builder.build()).submitAndDelete(10);
		}
		return HANDLED;
	}
	
	@NotNull
	private String handleException(@NotNull Throwable e){
		if(e instanceof CompletionException completionException){
			return handleException(completionException.getCause());
		}
		
		return e instanceof ErrorResponseException ? ((ErrorResponseException) e).getMeaning() : e.getMessage();
	}
}
