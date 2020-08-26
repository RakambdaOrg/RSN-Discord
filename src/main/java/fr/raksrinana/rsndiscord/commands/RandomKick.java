package fr.raksrinana.rsndiscord.commands;

import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.log.Log;
import fr.raksrinana.rsndiscord.utils.permission.Permission;
import fr.raksrinana.rsndiscord.utils.permission.SimplePermission;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@BotCommand
public class RandomKick extends BasicCommand{
	@Override
	public void addHelp(@NonNull Guild guild, @NonNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("reason", translate(guild, "command.random-kick.help.reason"), false);
	}
	
	@Override
	public @NonNull Permission getPermission(){
		return new SimplePermission("command.random-kick", false);
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
		
		randomKick(event.getAuthor(), event.getChannel(), targetRole, reason);
		return CommandResult.SUCCESS;
	}
	
	public static void randomKick(@NonNull User author, @NonNull TextChannel channel, Role targetRole, String reason){
		var guild = channel.getGuild();
		var botMember = guild.getSelfMember();
		guild.findMembers(member -> botMember.canInteract(member)
				&& Optional.ofNullable(targetRole).map(role -> member.getRoles().contains(role)).orElse(true))
				.onSuccess(members -> {
					if(members.isEmpty()){
						Actions.sendMessage(channel, translate(guild, "random-kick.no-member"), null);
					}
					else{
						var member = members.get(ThreadLocalRandom.current().nextInt(members.size()));
						Settings.get(guild).getRandomKick()
								.getKickedRole()
								.flatMap(RoleConfiguration::getRole)
								.ifPresent(kickedRole -> Actions.giveRole(member, kickedRole));
						Actions.sendMessage(channel, translate(guild, "random-kick.kicking", member.getAsMention()), null, false, action -> action.tts(true));
						
						Main.getExecutorService().schedule(() -> {
							Actions.kick(author, member, reason)
									.thenAccept(empty2 -> Actions.sendMessage(channel,
											translate(guild, "random-kick.kicked", member.getAsMention(), reason),
											null,
											false,
											action -> action.mentionUsers(member.getIdLong())))
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
