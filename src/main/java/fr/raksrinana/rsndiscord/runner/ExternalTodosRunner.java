package fr.raksrinana.rsndiscord.runner;

import fr.raksrinana.rsndiscord.api.externaltodos.ExternalTodosApi;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.reaction.WaitingReactionMessageConfiguration;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import static fr.raksrinana.rsndiscord.api.externaltodos.data.Status.EXTERNAL;
import static fr.raksrinana.rsndiscord.reaction.ReactionTag.EXTERNAL_TODO;
import static fr.raksrinana.rsndiscord.reaction.ReactionUtils.DELETE_KEY;
import static fr.raksrinana.rsndiscord.utils.BasicEmotes.CHECK_OK;
import static fr.raksrinana.rsndiscord.utils.BasicEmotes.CROSS_NO;
import static java.util.concurrent.TimeUnit.MINUTES;

@ScheduledRunner
public class ExternalTodosRunner implements IScheduledRunner{
	private final JDA jda;
	
	public ExternalTodosRunner(@NotNull JDA jda){
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
	
	@Override
	public void execute(){
		jda.getGuilds().forEach(guild -> {
			var configuration = Settings.get(guild).getExternalTodos();
			configuration.getEndpoint().ifPresent(endpoint -> {
				var token = configuration.getToken().orElse(null);
				
				configuration.getNotificationChannel()
						.flatMap(ChannelConfiguration::getChannel)
						.ifPresent(channel -> {
							var response = ExternalTodosApi.getTodos(endpoint, token);
							
							response.ifPresent(todos -> todos.getTodos().forEach(todo -> JDAWrappers.message(channel, "`" + todo.getKind().name() + "` => " + todo.getDescription()).submit()
									.thenAccept(message -> {
										JDAWrappers.addReaction(message, CHECK_OK).submit();
										if(todo.getKind().isCancellable()){
											JDAWrappers.addReaction(message, CROSS_NO).submit();
										}
										
										var waitingReactionMessageConfiguration = new WaitingReactionMessageConfiguration(message,
												EXTERNAL_TODO, Map.of(DELETE_KEY, Boolean.toString(false)));
										Settings.get(guild).addMessagesAwaitingReaction(waitingReactionMessageConfiguration);
										
										ExternalTodosApi.setStatus(endpoint, token, todo, EXTERNAL);
									})));
						});
			});
		});
	}
	
	@NotNull
	@Override
	public String getName(){
		return "External todos fetcher";
	}
	
	@NotNull
	@Override
	public TimeUnit getPeriodUnit(){
		return MINUTES;
	}
}
