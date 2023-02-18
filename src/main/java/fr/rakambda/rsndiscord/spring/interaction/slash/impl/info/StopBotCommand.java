package fr.rakambda.rsndiscord.spring.interaction.slash.impl.info;

import fr.rakambda.rsndiscord.spring.StopService;
import fr.rakambda.rsndiscord.spring.interaction.slash.api.IExecutableSlashCommandGuild;
import fr.rakambda.rsndiscord.spring.interaction.slash.api.IExecutableSlashCommandUser;
import fr.rakambda.rsndiscord.spring.interaction.slash.permission.CreatorPermission;
import fr.rakambda.rsndiscord.spring.interaction.slash.permission.IPermission;
import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class StopBotCommand implements IExecutableSlashCommandGuild, IExecutableSlashCommandUser {
	private final StopService stopService;

	@Autowired
	public StopBotCommand(StopService stopService) {
		this.stopService = stopService;
	}

	@Override
	@NotNull
	public String getId() {
		return "stop";
	}

	@Override
	@NotNull
	public String getPath() {
		return "bot/stop";
	}

	@Override
	@NotNull
	public IPermission getPermission() {
		return new CreatorPermission();
	}

	@Override
	@NotNull
	public CompletableFuture<?> executeGuild(@NotNull SlashCommandInteraction event, @NotNull Guild guild, @NotNull Member member) {
		return execute(event);
	}

	@Override
	@NotNull
	public CompletableFuture<?> executeUser(@NotNull SlashCommandInteraction event) {
		return execute(event);
	}

	@NotNull
	private CompletableFuture<Message> execute(@NotNull SlashCommandInteraction event) {
		try {
			var reply = JDAWrappers.reply(event, "Stopping").ephemeral(true).submit();
			stopService.shutdown(event.getJDA());
			return reply;
		} catch (InterruptedException e) {
			throw new RuntimeException("Failed to stop bot", e);
		}
	}
}
