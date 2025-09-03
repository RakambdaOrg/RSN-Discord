package fr.rakambda.rsndiscord.spring.event;

import fr.rakambda.rsndiscord.spring.interaction.InteractionsService;
import fr.rakambda.rsndiscord.spring.interaction.context.message.MessageContextMenuService;
import fr.rakambda.rsndiscord.spring.interaction.slash.SlashCommandService;
import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import fr.rakambda.rsndiscord.spring.settings.ApplicationSettings;
import fr.rakambda.rsndiscord.spring.storage.entity.AudioEntity;
import fr.rakambda.rsndiscord.spring.storage.entity.GuildEntity;
import fr.rakambda.rsndiscord.spring.storage.repository.AudioRepository;
import fr.rakambda.rsndiscord.spring.storage.repository.GuildRepository;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import static net.dv8tion.jda.api.OnlineStatus.ONLINE;

@Slf4j
@Component
public class ReadyEventListener extends ListenerAdapter{
	private final ApplicationSettings applicationSettings;
	private final InteractionsService interactionsService;
	private final SlashCommandService slashCommandService;
	private final MessageContextMenuService messageContextMenuService;
	private final GuildRepository guildRepository;
	private final AudioRepository audioRepository;
	
	@Autowired
	public ReadyEventListener(ApplicationSettings applicationSettings, InteractionsService interactionsService, SlashCommandService slashCommandService, MessageContextMenuService messageContextMenuService, GuildRepository guildRepository, AudioRepository audioRepository){
		this.applicationSettings = applicationSettings;
		this.interactionsService = interactionsService;
		this.slashCommandService = slashCommandService;
		this.messageContextMenuService = messageContextMenuService;
		this.guildRepository = guildRepository;
		this.audioRepository = audioRepository;
	}
	
	@Override
	public void onReady(@NonNull ReadyEvent event){
		var jda = event.getJDA();
		
		log.info("JDA ready");
		JDAWrappers.editPresence(jda)
				.setStatus(ONLINE)
				.setActivity(Activity.of(Activity.ActivityType.PLAYING, "Bip bip"));
		
		clearGlobalCommands(jda)
				.thenCompose(empty -> registerCommands(jda))
				.exceptionally(this::handleException);
	}
	
	@Nullable
	private <T> T handleException(@NonNull Throwable throwable){
		log.error("Failed to handle commands", throwable);
		return null;
	}
	
	@Override
	public void onGuildReady(@NonNull GuildReadyEvent event){
		log.info("Guild ready {}", event.getGuild());
		ensureGuildStorage(event);
	}
	
	@Override
	public void onGuildJoin(@NonNull GuildJoinEvent event){
		log.info("Guild joined {}", event.getGuild());
		ensureGuildStorage(event);
	}
	
	private void ensureGuildStorage(@NonNull GenericGuildEvent event){
		log.info("Adding {} to storage", event.getGuild());
		
		var guildId = event.getGuild().getIdLong();
		var guildName = event.getGuild().getName();
		guildRepository.save(guildRepository.findById(guildId)
				.map(e -> e.withName(guildName))
				.orElseGet(() -> GuildEntity.builder()
						.id(guildId)
						.name(guildName)
						.build()));
		
		if(!audioRepository.existsByGuildId(guildId)){
			audioRepository.save(AudioEntity.builder().volume(100).guildId(guildId).build());
		}
	}
	
	@NonNull
	private CompletableFuture<Void> clearGlobalCommands(JDA jda){
		return jda.updateCommands().submit()
				.thenCompose(empty -> clearGuildCommands(jda.getGuilds()));
	}
	
	@NonNull
	private CompletableFuture<Void> clearGuildCommands(@NonNull Collection<Guild> guilds){
		return guilds.stream()
				.map(interactionsService::clearGuildCommands)
				.reduce((left, right) -> left.thenCombine(right, (v1, v2) -> null))
				.orElseGet(() -> CompletableFuture.completedFuture(null));
	}
	
	@NonNull
	private CompletableFuture<Void> registerCommands(@NonNull JDA jda){
		return slashCommandService.registerGlobalCommands(jda)
				.thenCompose(empty -> messageContextMenuService.registerGlobalMenus(jda))
				.thenCompose(empty -> registerGuildCommands(jda.getGuilds()));
	}
	
	private CompletableFuture<Void> registerGuildCommands(@NonNull Collection<Guild> guilds){
		return guilds.stream()
				.map(guild -> slashCommandService.registerGuildCommands(guild)
						.thenCompose(empty -> messageContextMenuService.registerGuildMenus(guild)))
				.reduce((left, right) -> left.thenCombine(right, (v1, v2) -> null))
				.orElseGet(() -> CompletableFuture.completedFuture(null));
	}
}
