package fr.raksrinana.rsndiscord.command.impl.moderation;

import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.command.base.group.SubCommand;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import static fr.raksrinana.rsndiscord.command.CommandResult.HANDLED;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static net.dv8tion.jda.api.interactions.commands.OptionType.ROLE;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

@Log4j2
public class RandomKickCommand extends SubCommand{
	private static final String MESSAGE_OPTION_ID = "message";
	private static final String ROLE_OPTION_ID = "role";
	
	@Override
	@NotNull
	public String getId(){
		return "random-kick";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Randomly kick a person";
	}
	
	@Override
	@NotNull
	protected Collection<? extends OptionData> getOptions(){
		return List.of(
				new OptionData(STRING, MESSAGE_OPTION_ID, "Random kick message").setRequired(true),
				new OptionData(ROLE, ROLE_OPTION_ID, "Role in which to chose a person").setRequired(true));
	}
	
	@Override
	@NotNull
	public CommandResult execute(@NotNull SlashCommandEvent event){
		var targetRole = Optional.ofNullable(event.getOption(ROLE_OPTION_ID)).map(OptionMapping::getAsRole).orElse(null);
		var reason = event.getOption(MESSAGE_OPTION_ID).getAsString();
		
		JDAWrappers.edit(event, "Random kick started");
		randomKick(event.getTextChannel(), targetRole, reason, true);
		
		return HANDLED;
	}
	
	public static Optional<Role> getRandomRole(@NotNull Guild guild){
		var chance = ThreadLocalRandom.current().nextDouble();
		var randomKickConfiguration = Settings.get(guild).getRandomKick();
		
		if(chance < randomKickConfiguration.getKickRoleProbability().orElse(0.25)){
			var roles = randomKickConfiguration.getKickableRoles();
			var roleIndex = ThreadLocalRandom.current().nextInt(roles.size());
			return roles.stream().skip(roleIndex)
					.findFirst()
					.flatMap(RoleConfiguration::getRole);
		}
		return Optional.empty();
	}
	
	public static void randomKick(@NotNull TextChannel channel, @Nullable Role targetRole, @NotNull String reason, boolean allowReKick){
		var guild = channel.getGuild();
		var botMember = guild.getSelfMember();
		
		guild.findMembers(member -> botMember.canInteract(member)
				&& Optional.ofNullable(targetRole).map(role -> member.getRoles().contains(role)).orElse(true))
				.onSuccess(members -> {
					if(members.isEmpty()){
						JDAWrappers.message(channel, translate(guild, "random-kick.no-member")).submit();
					}
					else{
						performKick(guild, channel, members, reason, allowReKick);
					}
				})
				.onError(e -> {
					log.error("Failed to load members", e);
					JDAWrappers.message(channel, translate(guild, "random-kick.error-members")).submit();
				});
	}
	
	private static void performKick(@NotNull Guild guild, @NotNull TextChannel channel, @NotNull List<Member> members, @NotNull String reason, boolean allowReKick){
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
				.ifPresent(kickedRole -> JDAWrappers.addRole(member, kickedRole).submit());
		
		member.getUser().openPrivateChannel().submit()
				.thenAccept(privateChannel -> {
					JDAWrappers.message(privateChannel, kickMessage).submit();
					
					Optional.ofNullable(guild.getDefaultChannel())
							.map(TextChannel::createInvite)
							.map(invite -> invite.setMaxAge(24L, TimeUnit.HOURS)
									.setMaxUses(1))
							.map(RestAction::submit)
							.ifPresent(invite -> invite.thenAccept(inv -> JDAWrappers.message(privateChannel, inv.getUrl()).submit()));
				});
		
		JDAWrappers.message(channel, translate(guild, "random-kick.kicking", member.getAsMention()))
				.tts(true).submit()
				.thenAccept(kickStartMessage -> Main.getExecutorService().schedule(() -> {
					JDAWrappers.kick(member, reason).submit()
							.thenAccept(empty2 -> JDAWrappers.reply(kickStartMessage, kickMessage).mention(member).submit())
							.exceptionally(exception -> {
								JDAWrappers.message(channel, translate(guild, "random-kick.error", exception.getMessage())).submit();
								return null;
							});
				}, 30, TimeUnit.SECONDS));
	}
}
