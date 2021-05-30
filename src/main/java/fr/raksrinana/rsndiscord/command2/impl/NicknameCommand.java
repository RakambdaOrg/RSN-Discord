package fr.raksrinana.rsndiscord.command2.impl;

import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.command2.BotSlashCommand;
import fr.raksrinana.rsndiscord.command2.base.SimpleCommand;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.schedule.ScheduleUtils.deleteMessageMins;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static fr.raksrinana.rsndiscord.utils.Utilities.durationToString;
import static java.awt.Color.*;

@BotSlashCommand
public class NicknameCommand extends SimpleCommand{
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
	protected @NotNull Collection<? extends OptionData> getOptions(){
		return List.of(new OptionData(OptionType.STRING, NICK_OPTION_ID, "The nickname to set (leave empty to reset it)"));
	}
	
	@Override
	public boolean replyEphemeral(){
		return false;
	}
	
	@Override
	@NotNull
	public CommandResult execute(@NotNull SlashCommandEvent event){
		var guild = event.getGuild();
		var member = event.getMember();
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
		var delay = Duration.ofSeconds(nicknameConfiguration.getChangeDelay());
		
		if(newNickname.equals(oldNickname)){
			builder.setColor(ORANGE)
					.setTitle(translate(guild, "nickname.no-changes"))
					.addField(translate(guild, "nickname.reason"), translate(guild, "nickname.same-nickname"), false);
			JDAWrappers.replyCommand(event, builder.build()).submit().thenAccept(deleteMessageMins(10));
			return SUCCESS;
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
			JDAWrappers.replyCommand(event, builder.build()).submit().thenAccept(deleteMessageMins(10));
			return SUCCESS;
		}
		
		try{
			JDAWrappers.modifyNickname(member, newNickname.orElse(null)).submit()
					.thenAccept(empty -> {
						Log.getLogger(guild).info("{} renamed from `{}` to `{}`", member, oldNickname, newNickname);
						
						var newLastChange = ZonedDateTime.now();
						nicknameConfiguration.setLastChange(user, newLastChange);
						
						builder.setColor(GREEN)
								.addField(translate(guild, "nickname.next-allowed"), newLastChange.plus(delay).format(DF), false);
						
						JDAWrappers.replyCommand(event, builder.build()).submit();
					})
					.exceptionally(error -> {
						var errorMessage = error instanceof ErrorResponseException ? ((ErrorResponseException) error).getMeaning() : error.getMessage();
						builder.setColor(RED)
								.setTitle(translate(guild, "nickname.invalid"))
								.addField(translate(guild, "nickname.reason"), errorMessage, false);
						JDAWrappers.replyCommand(event, builder.build()).submit().thenAccept(deleteMessageMins(10));
						return null;
					});
		}
		catch(HierarchyException e){
			builder.setColor(RED)
					.addField(translate(guild, "nickname.reason"), translate(guild, "nickname.target-error"), false);
			JDAWrappers.replyCommand(event, builder.build()).submit().thenAccept(deleteMessageMins(10));
		}
		return SUCCESS;
	}
}
