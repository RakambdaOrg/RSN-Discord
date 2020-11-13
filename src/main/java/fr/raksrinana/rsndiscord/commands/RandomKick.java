package fr.raksrinana.rsndiscord.commands;

import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.modules.permission.IPermission;
import fr.raksrinana.rsndiscord.modules.permission.SimplePermission;
import fr.raksrinana.rsndiscord.modules.settings.Settings;
import fr.raksrinana.rsndiscord.modules.settings.types.RoleConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.requests.RestAction;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@BotCommand
public class RandomKick extends BasicCommand{
	public static Optional<Role> getRandomRole(@NonNull Guild guild){
		var chance = ThreadLocalRandom.current().nextDouble();
		if(chance < Settings.get(guild).getRandomKick().getKickRoleProbability()){
			var roles = Settings.get(guild).getRandomKick().getKickableRoles();
			var roleIndex = ThreadLocalRandom.current().nextInt(roles.size());
			return roles.stream().skip(roleIndex).findFirst().flatMap(RoleConfiguration::getRole);
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
			return CommandResult.BAD_ARGUMENTS;
		}
		
		var targetRole = event.getMessage().getMentionedRoles().stream().findFirst().orElse(null);
		var reason = String.join(" ", args);
		
		randomKick(event.getAuthor(), event.getChannel(), targetRole, reason, true);
		return CommandResult.SUCCESS;
	}
	
	public static void randomKick(@NonNull User author, @NonNull TextChannel channel, Role targetRole, String reason, boolean allowReKick){
		var guild = channel.getGuild();
		var botMember = guild.getSelfMember();
		var kickRole = Settings.get(guild).getRandomKick()
				.getKickedRole()
				.flatMap(RoleConfiguration::getRole);
		guild.findMembers(member -> botMember.canInteract(member)
				&& Optional.ofNullable(targetRole).map(role -> member.getRoles().contains(role)).orElse(true))
				.onSuccess(members -> {
					if(members.isEmpty()){
						Actions.sendMessage(channel, translate(guild, "random-kick.no-member"), null);
					}
					else{
						if(!allowReKick){
							var noReKick = members.stream().filter(member -> !kickRole.map(r -> member.getRoles().contains(r)).orElse(false)).collect(Collectors.toList());
							if(!noReKick.isEmpty()){
								members = noReKick;
							}
						}
						
						var member = members.get(ThreadLocalRandom.current().nextInt(members.size()));
						var kickMessage = translate(guild, "random-kick.kicked", member.getAsMention(), reason);
						
						Settings.get(guild).getRandomKick()
								.getKickedRole()
								.flatMap(RoleConfiguration::getRole)
								.ifPresent(kickedRole -> Actions.giveRole(member, kickedRole));
						Actions.sendMessage(channel, translate(guild, "random-kick.kicking", member.getAsMention()), null, false, action -> action.tts(true));
						Actions.sendPrivateMessage(guild, member.getUser(), kickMessage, null);
						
						Optional.ofNullable(guild.getDefaultChannel())
								.map(TextChannel::createInvite)
								.map(invite -> invite
										.setMaxAge(24L, TimeUnit.HOURS)
										.setMaxUses(1))
								.map(RestAction::submit)
								.ifPresent(invite -> invite.thenAccept(inv -> Actions.sendPrivateMessage(guild, member.getUser(), inv.getUrl(), null)));
						
						Main.getExecutorService().schedule(() -> {
							Actions.kick(author, member, reason)
									.thenAccept(empty2 -> Actions.sendMessage(channel, kickMessage, null, false, action -> action.mentionUsers(member.getIdLong())))
									.exceptionally(exception -> {
										Actions.sendMessage(channel, translate(guild, "random-kick.error", exception.getMessage()), null);
										return null;
									});
						}, 30, TimeUnit.SECONDS);
					}
				}).onError(e -> {
			Log.getLogger(guild).error("Failed to load members", e);
			Actions.sendMessage(channel, translate(guild, "random-kick.error-members"), null);
		});
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
