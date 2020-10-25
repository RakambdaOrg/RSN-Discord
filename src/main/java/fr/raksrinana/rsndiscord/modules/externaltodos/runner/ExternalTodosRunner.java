package fr.raksrinana.rsndiscord.modules.externaltodos.runner;

import fr.raksrinana.rsndiscord.modules.externaltodos.ExternalTodosUtils;
import fr.raksrinana.rsndiscord.modules.externaltodos.data.Status;
import fr.raksrinana.rsndiscord.modules.reaction.ReactionTag;
import fr.raksrinana.rsndiscord.modules.reaction.ReactionUtils;
import fr.raksrinana.rsndiscord.modules.reaction.config.WaitingReactionMessageConfiguration;
import fr.raksrinana.rsndiscord.modules.settings.Settings;
import fr.raksrinana.rsndiscord.modules.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.runner.IScheduledRunner;
import fr.raksrinana.rsndiscord.runner.ScheduledRunner;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import lombok.NonNull;
import net.dv8tion.jda.api.JDA;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@ScheduledRunner
public class ExternalTodosRunner implements IScheduledRunner{
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z");
	private final JDA jda;
	
	public ExternalTodosRunner(@NonNull JDA jda){
		this.jda = jda;
	}
	
	@Override
	public long getDelay(){
		return 2;
	}
	
	@Override
	public long getPeriod(){
		return 15;
	}
	
	@NonNull
	@Override
	public TimeUnit getPeriodUnit(){
		return TimeUnit.MINUTES;
	}
	
	@Override
	public void execute(){
		this.jda.getGuilds().forEach(guild -> {
			final var configuration = Settings.get(guild).getExternalTodos();
			configuration.getEndpoint().ifPresent(endpoint -> {
				final var token = configuration.getToken().orElse(null);
				configuration.getNotificationChannel().flatMap(ChannelConfiguration::getChannel).ifPresent(channel -> {
					final var response = ExternalTodosUtils.getTodos(endpoint, token);
					response.ifPresent(todos -> todos.getTodos().forEach(todo ->
							Actions.sendMessage(channel, "`" + todo.getKind().name() + "` => " + todo.getDescription(), null).thenAccept(message -> {
								Actions.addReaction(message, BasicEmotes.CHECK_OK.getValue());
								if(todo.getKind().isCancellable()){
									Actions.addReaction(message, BasicEmotes.CROSS_NO.getValue());
								}
								Settings.get(guild).addMessagesAwaitingReaction(new WaitingReactionMessageConfiguration(message, ReactionTag.EXTERNAL_TODO, Map.of(ReactionUtils.DELETE_KEY, Boolean.toString(false))));
								ExternalTodosUtils.setStatus(endpoint, token, todo, Status.EXTERNAL);
							})));
				});
			});
		});
	}
	
	@NonNull
	@Override
	public String getName(){
		return "External todos fetcher";
	}
}
