package fr.raksrinana.rsndiscord.commands;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.log.Log;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import java.awt.Color;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@BotCommand
public class NicknameCommand extends BasicCommand{
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
	
	@Override
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("User", "The targeted user (default: @me)", false);
		builder.addField("Nickname", "The new surname (if none are provided, the old nickname will be removed)", false);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		final Optional<Member> memberOptional;
		if(event.getMessage().getMentionedUsers().isEmpty()){
			memberOptional = Optional.ofNullable(event.getMember());
		}
		else{
			args.pop();
			memberOptional = Optional.ofNullable(event.getGuild().retrieveMember(event.getMessage().getMentionedUsers().get(0)).complete());
			if(memberOptional.isPresent() && !Objects.equals(event.getAuthor(), memberOptional.get().getUser()) && !Utilities.isTeam(event.getMember())){
				final var builder = new EmbedBuilder();
				builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
				builder.addField("User", memberOptional.get().getAsMention(), true);
				builder.setTitle("You thought changing the name of another guy?");
				builder.setColor(Color.RED);
				Actions.reply(event, "", builder.build());
				return CommandResult.SUCCESS;
			}
		}
		memberOptional.ifPresentOrElse(member -> {
			final var oldName = Optional.ofNullable(member.getNickname());
			final var lastChange = Settings.get(event.getGuild()).getNicknameConfiguration().getLastChange(member.getUser());
			final var delay = Duration.ofSeconds(Settings.get(event.getGuild()).getNicknameConfiguration().getChangeDelay());
			final String newName;
			if(args.isEmpty()){
				newName = null;
			}
			else{
				newName = String.join(" ", args);
			}
			final var builder = new EmbedBuilder();
			builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
			if(Objects.equals(newName, oldName.orElse(null))){
				builder.setColor(Color.ORANGE);
				builder.setTitle("No changes");
				builder.addField("Reason", "Nickname is the same as the current one", false);
				Actions.reply(event, "", builder.build());
			}
			else if(Objects.nonNull(newName) && !Utilities.isTeam(event.getMember()) && lastChange.map(date -> date.plus(delay)).map(date -> date.isAfter(ZonedDateTime.now())).orElse(true)){
				builder.setColor(Color.RED);
				builder.addField("Old nickname", oldName.orElse("*NONE*"), true);
				builder.addField("User", member.getAsMention(), true);
				builder.addField("Reason", "You can change your nickname once every " + delay.toString().replace("PT", ""), true);
				builder.addField("Last change", lastChange.map(date -> date.format(DF)).orElse("<NONE>"), true);
				builder.setTimestamp(ZonedDateTime.now());
				Actions.reply(event, "", builder.build());
			}
			else{
				builder.addField("Old nickname", oldName.orElse("*NONE*"), true);
				builder.addField("New nickname", Objects.isNull(newName) ? "*NONE*" : newName, true);
				builder.addField("User", member.getAsMention(), true);
				try{
					Actions.changeNickname(member, newName);
					builder.setColor(Color.GREEN);
					Settings.get(event.getGuild()).getNicknameConfiguration().setLastChange(member.getUser(), Objects.isNull(newName) ? null : ZonedDateTime.now());
					Settings.get(event.getGuild()).getNicknameConfiguration().getLastChange(member.getUser()).map(d -> d.plus(delay)).filter(d -> d.isAfter(ZonedDateTime.now())).ifPresent(d -> builder.addField("Next allowed change", d.format(DF), false));
					Log.getLogger(event.getGuild()).info("{} renamed {} from `{}` to `{}`", event.getAuthor(), member.getUser(), oldName, newName);
				}
				catch(final HierarchyException e){
					builder.setColor(Color.RED);
					builder.setTitle("You thought I can change the nickname of someone higher than me?!");
				}
				catch(final ErrorResponseException e){
					builder.setColor(Color.RED);
					builder.setTitle("Invalid nickname");
					builder.addField("Reason", e.getMeaning(), false);
				}
				Actions.reply(event, "", builder.build());
			}
		}, () -> Actions.reply(event, "Member not found", null));
		return CommandResult.SUCCESS;
	}
	
	@NonNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " [@user] [nickname]";
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Nickname";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("nickname", "nick");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Change the nickname of a user";
	}
}
