package fr.raksrinana.rsndiscord.interaction.command.slash.impl;

import fr.raksrinana.rsndiscord.interaction.command.CommandResult;
import fr.raksrinana.rsndiscord.interaction.command.slash.api.BotSlashCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.base.SimpleSlashCommand;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import static fr.raksrinana.rsndiscord.interaction.command.CommandResult.HANDLED;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static fr.raksrinana.rsndiscord.utils.Utilities.durationToString;
import static java.awt.Color.*;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

@BotSlashCommand
@Log4j2
public class NicknameCommand extends SimpleSlashCommand{
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
	private static final String NICK_OPTION_ID = "nick";
	
	@Override
	@NotNull
	public String getId(){
		return "nickname";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Change your nickname";
	}
	
	@Override
	@NotNull
	protected Collection<? extends OptionData> getOptions(){
		return List.of(new OptionData(STRING, NICK_OPTION_ID, "The nickname to set (leave empty to reset it)"));
	}
	
	@Override
	public boolean replyEphemeral(){
		return false;
	}
	
	@Override
	@NotNull
	public CommandResult executeGuild(@NotNull SlashCommandInteractionEvent event, @NotNull Guild guild, @NotNull Member member){
		var user = event.getUser();
		
		var oldNickname = Optional.ofNullable(member.getNickname());
		var newNickname = Optional.ofNullable(event.getOption(NICK_OPTION_ID))
				.map(OptionMapping::getAsString)
				.filter(val -> !val.isBlank());
		
		var builder = new EmbedBuilder()
				.setAuthor(user.getName(), null, user.getAvatarUrl())
				.addField(translate(guild, "nickname.user"), user.getAsMention(), true)
				.addField(translate(guild, "nickname.old-nick"), oldNickname.orElseGet(() -> translate(guild, "nickname.unknown")), true)
				.addField(translate(guild, "nickname.new-nick"), newNickname.orElseGet(() -> translate(guild, "nickname.unknown")), true)
				.setTimestamp(ZonedDateTime.now());
		
		var nicknameConfiguration = Settings.get(guild).getNicknameConfiguration();
		var lastChange = nicknameConfiguration.getLastChange(user);
		var delay = Duration.ofSeconds(nicknameConfiguration.getChangeDelay().orElse(3600L));
		
		if(newNickname.equals(oldNickname)){
			builder.setColor(ORANGE)
					.setTitle(translate(guild, "nickname.no-changes"))
					.addField(translate(guild, "nickname.reason"), translate(guild, "nickname.same-nickname"), false);
			JDAWrappers.edit(event, builder.build()).submitAndDelete(10);
			return HANDLED;
		}
		
		var isStillInCooldown = lastChange.map(date -> date.plus(delay))
				.map(date -> date.isAfter(ZonedDateTime.now()))
				.orElse(false);
		
		if(newNickname.isPresent() && isStillInCooldown){
			var lastChangeStr = lastChange.map(date -> date.format(DF))
					.orElseGet(() -> translate(guild, "nickname.unknown"));
			var nextChange = lastChange.map(date -> date.plus(delay))
					.map(date -> date.format(DF))
					.orElseGet(() -> translate(guild, "nickname.unknown"));
			
			builder.setColor(RED)
					.addField(translate(guild, "nickname.reason"), translate(guild, "nickname.cooldown", durationToString(delay)), true)
					.addField(translate(guild, "nickname.last-change"), lastChangeStr, true)
					.addField(translate(guild, "nickname.next-allowed"), nextChange, true);
			JDAWrappers.edit(event, builder.build()).submitAndDelete(10);
			return HANDLED;
		}
		
		try{
			JDAWrappers.modifyNickname(member, newNickname.orElse(null)).submit()
					.thenAccept(empty -> {
						log.info("{} renamed from `{}` to `{}`", member, oldNickname, newNickname);
						
						var newLastChange = ZonedDateTime.now();
						nicknameConfiguration.setLastChange(user, newLastChange);
						
						builder.setColor(GREEN)
								.addField(translate(guild, "nickname.next-allowed"), newLastChange.plus(delay).format(DF), false);
						
						JDAWrappers.edit(event, builder.build()).submit();
					})
					.exceptionally(error -> {
						var errorMessage = error instanceof ErrorResponseException ? ((ErrorResponseException) error).getMeaning() : error.getMessage();
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
}
