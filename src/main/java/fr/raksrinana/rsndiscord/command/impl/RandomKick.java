package fr.raksrinana.rsndiscord.command.impl;

import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.command.BasicCommand;
import fr.raksrinana.rsndiscord.command.BotCommand;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.requests.RestAction;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import static fr.raksrinana.rsndiscord.command.CommandResult.BAD_ARGUMENTS;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@BotCommand
public class RandomKick extends BasicCommand{
	public static Optional<Role> getRandomRole(@NonNull Guild guild){
		var chance = ThreadLocalRandom.current().nextDouble();
		var randomKickConfiguration = Settings.get(guild).getRandomKick();
		
		if(chance < randomKickConfiguration.getKickRoleProbability()){
			var roles = randomKickConfiguration.getKickableRoles();
			var roleIndex = ThreadLocalRandom.current().nextInt(roles.size());
			return roles.stream().skip(roleIndex)
					.findFirst()
					.flatMap(RoleConfiguration::getRole);
		}
		return Optional.empty();
	}
	
	@Override
	public void addHelp(@NonNull Guild guild, @NonNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("reason", translate(guild, "command.random-kick.help.reason"), false);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		if(args.isEmpty()){
			return BAD_ARGUMENTS;
		}
		
		var targetRole = getFirstRoleMentioned(event).orElse(null);
		var reason = String.join(" ", args);
		
		randomKick(event.getChannel(), targetRole, reason, true);
		return SUCCESS;
	}
	
	public static void randomKick(@NonNull TextChannel channel, Role targetRole, String reason, boolean allowReKick){
		var guild = channel.getGuild();
		var botMember = guild.getSelfMember();
		
		guild.findMembers(member -> botMember.canInteract(member)
				&& Optional.ofNullable(targetRole).map(role -> member.getRoles().contains(role)).orElse(true))
				.onSuccess(members -> {
					if(members.isEmpty()){
						channel.sendMessage(translate(guild, "random-kick.no-member")).submit();
					}
					else{
						performKick(guild, channel, members, reason, allowReKick);
					}
				})
				.onError(e -> {
					Log.getLogger(guild).error("Failed to load members", e);
					channel.sendMessage(translate(guild, "random-kick.error-members")).submit();
				});
	}
	
	private static void performKick(Guild guild, TextChannel channel, List<Member> members, String reason, boolean allowReKick){
		var kickRole = Settings.get(guild).getRandomKick()
				.getKickedRole()
				.flatMap(RoleConfiguration::getRole);
		
		if(!allowReKick){
			var noReKick = members.stream()
					.filter(member -> !kickRole.map(r -> member.getRoles().contains(r)).orElse(false))
					.collect(Collectors.toList());
			if(!noReKick.isEmpty()){
				members = noReKick;
			}
		}
		
		var member = members.get(ThreadLocalRandom.current().nextInt(members.size()));
		var kickMessage = translate(guild, "random-kick.kicked", member.getAsMention(), reason);
		
		Settings.get(guild).getRandomKick()
				.getKickedRole()
				.flatMap(RoleConfiguration::getRole)
				.ifPresent(kickedRole -> guild.addRoleToMember(member, kickedRole).submit());
		
		member.getUser().openPrivateChannel().submit()
				.thenAccept(privateChannel -> {
					privateChannel.sendMessage(kickMessage).submit();
					
					Optional.ofNullable(guild.getDefaultChannel())
							.map(TextChannel::createInvite)
							.map(invite -> invite.setMaxAge(24L, TimeUnit.HOURS)
									.setMaxUses(1))
							.map(RestAction::submit)
							.ifPresent(invite -> invite.thenAccept(inv -> privateChannel.sendMessage(inv.getUrl()).submit()));
				});
		
		channel.sendMessage(translate(guild, "random-kick.kicking", member.getAsMention()))
				.tts(true).submit()
				.thenAccept(kickStartMessage -> Main.getExecutorService().schedule(() -> {
					member.kick(reason).submit()
							.thenAccept(empty2 -> kickStartMessage.reply(kickMessage)
									.mentionUsers(member.getIdLong()).submit())
							.exceptionally(exception -> {
								channel.sendMessage(translate(guild, "random-kick.error", exception.getMessage())).submit();
								return null;
							});
				}, 30, TimeUnit.SECONDS));
	}
	
	@Override
	public @NonNull String getCommandUsage(){
		return super.getCommandUsage() + "[@role] <reason>";
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return new SimplePermission("command.random-kick", false);
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.random-kick.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("randomKick");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.random-kick.description");
	}
}
