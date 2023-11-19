package fr.rakambda.rsndiscord.spring.event;

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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import static net.dv8tion.jda.api.OnlineStatus.ONLINE;

@Slf4j
@Component
public class ReadyEventListener extends ListenerAdapter{
	private final ApplicationSettings applicationSettings;
	private final SlashCommandService slashCommandService;
	private final GuildRepository guildRepository;
	private final AudioRepository audioRepository;
	
	@Autowired
	public ReadyEventListener(ApplicationSettings applicationSettings, SlashCommandService slashCommandService, GuildRepository guildRepository, AudioRepository audioRepository){
		this.applicationSettings = applicationSettings;
		this.slashCommandService = slashCommandService;
		this.guildRepository = guildRepository;
		this.audioRepository = audioRepository;
	}
	
	@Override
	public void onReady(@NotNull ReadyEvent event){
		var jda = event.getJDA();
		
		log.info("JDA ready");
		JDAWrappers.editPresence(jda)
				.setStatus(ONLINE)
				.setActivity(Activity.of(Activity.ActivityType.PLAYING, "Bip bip"));
		
		jda.getGuilds().forEach(Guild::loadMembers);
		
		clearGuildCommands(jda.getGuilds())
				.thenCompose(empty -> registerCommands(jda))
				.exceptionally(this::handleException);
	}
	
	@Nullable
	private <T> T handleException(@NotNull Throwable throwable){
		log.error("Failed to handle commands", throwable);
		return null;
	}
	
	@Override
	public void onGuildReady(@NotNull GuildReadyEvent event){
		log.info("Guild ready {}", event.getGuild());
		ensureGuildStorage(event);
	}
	
	@Override
	public void onGuildJoin(@NotNull GuildJoinEvent event){
		log.info("Guild joined {}", event.getGuild());
		ensureGuildStorage(event);
	}
	
	private void ensureGuildStorage(@NotNull GenericGuildEvent event){
		var guildId = event.getGuild().getIdLong();
		if(guildRepository.existsById(guildId)){
			return;
		}
		
		log.info("Adding {} to storage", event.getGuild());
		var guildEntity = guildRepository.save(GuildEntity.builder().id(guildId).build());
		audioRepository.save(AudioEntity.builder().volume(100).guildId(guildId).build());
	}
	
	@NotNull
	private CompletableFuture<Void> clearGuildCommands(@NotNull Collection<Guild> guilds){
		var future = CompletableFuture.<Void> completedFuture(null);
		for(var guild : guilds){
			future = future.thenCompose(empty2 -> slashCommandService.clearGuildCommands(guild));
		}
		return future;
	}
	
	@NotNull
	private CompletableFuture<Void> registerCommands(@NotNull JDA jda){
		if(applicationSettings.isDevelopment()){
			return applicationSettings.getDevelopmentGuilds().stream()
					.map(jda::getGuildById)
					.filter(Objects::nonNull)
					.map(slashCommandService::registerGuildCommands)
					.reduce((left, right) -> left.thenCombine(right, (v1, v2) -> null))
					.orElseGet(() -> CompletableFuture.completedFuture(null));
		}
		
		return slashCommandService.registerGlobalCommands(jda);
	}
}
