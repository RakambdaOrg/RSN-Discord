package fr.raksrinana.rsndiscord.utils.jda.wrappers;

import fr.raksrinana.rsndiscord.log.Log;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.managers.ChannelManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.concurrent.CompletableFuture;

public class EditChannelWrapper{
	private final Guild guild;
	private final TextChannel channel;
	private ChannelManager action;
	
	public EditChannelWrapper(@NotNull Guild guild, @NotNull TextChannel channel){
		this.guild = guild;
		this.channel = channel;
		this.action = channel.getManager();
	}
	@NotNull
	public EditChannelWrapper sync(@NotNull Category category){
		Log.getLogger(guild).info("Will sync permissions of {} with {}", channel, category);
		action = action.sync(category);
		return this;
	}
	
	@NotNull
	public EditChannelWrapper setParent(@Nullable Category category){
		Log.getLogger(guild).info("Will set {} in category {}", channel, category);
		action = action.setParent(category);
		return this;
	}
	
	@NotNull
	public CompletableFuture<Void> submit(){
		return action.submit()
				.thenAccept(empty -> Log.getLogger(guild).info("Edited channel {}", channel));
	}
}
