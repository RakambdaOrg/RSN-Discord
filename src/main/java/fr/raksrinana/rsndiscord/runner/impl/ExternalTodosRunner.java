package fr.raksrinana.rsndiscord.runner.impl;

import fr.raksrinana.rsndiscord.api.externaltodos.ExternalTodosApi;
import fr.raksrinana.rsndiscord.components.impl.button.ExternalTodoCompletedButtonHandler;
import fr.raksrinana.rsndiscord.components.impl.button.ExternalTodoDiscardedButtonHandler;
import fr.raksrinana.rsndiscord.runner.api.IScheduledRunner;
import fr.raksrinana.rsndiscord.runner.api.ScheduledRunner;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import static fr.raksrinana.rsndiscord.api.externaltodos.data.Status.EXTERNAL;
import static java.util.concurrent.TimeUnit.MINUTES;

@ScheduledRunner
public class ExternalTodosRunner implements IScheduledRunner{
	@Override
	public long getDelay(){
		return 2;
	}
	
	@Override
	public long getPeriod(){
		return 15;
	}
	
	@Override
	public void executeGlobal(@NotNull JDA jda){
	}
	
	@Override
	public void executeGuild(@NotNull Guild guild) throws Exception{
		var configuration = Settings.get(guild).getExternalTodos();
		configuration.getEndpoint().ifPresent(endpoint -> {
			var token = configuration.getToken().orElse(null);
			
			configuration.getNotificationChannel()
					.flatMap(ChannelConfiguration::getChannel)
					.ifPresent(channel -> {
						var response = ExternalTodosApi.getTodos(endpoint, token);
						
						response.ifPresent(todos -> todos.getTodos()
								.forEach(todo -> {
									var components = new ArrayList<ItemComponent>();
									components.add(new ExternalTodoCompletedButtonHandler().asComponent());
									
									if(todo.getKind().isCancellable()){
										components.add(new ExternalTodoDiscardedButtonHandler().asComponent());
									}
									
									JDAWrappers.message(channel, "`" + todo.getKind().name() + "` => " + todo.getDescription())
											.addActionRow(components)
											.submit()
											.thenAccept(message -> ExternalTodosApi.setStatus(endpoint, token, todo, EXTERNAL));
								}));
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
