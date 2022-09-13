package fr.raksrinana.rsndiscord.utils.jda.wrappers.channel;

import fr.raksrinana.rsndiscord.utils.jda.ActionWrapper;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.managers.channel.concrete.TextChannelManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Log4j2
public class EditChannelWrapper extends ActionWrapper<Void, TextChannelManager>{
	private final TextChannel channel;
	
	public EditChannelWrapper(@NotNull TextChannel channel){
		super(channel.getManager());
		this.channel = channel;
	}
	
	@NotNull
	public EditChannelWrapper sync(@NotNull Category category){
		log.info("Will sync permissions of {} with {}", channel, category);
		getAction().sync(category);
		return this;
	}
	
	@NotNull
	public EditChannelWrapper setParent(@Nullable Category category){
		log.info("Will set {} in category {}", channel, category);
		getAction().setParent(category);
		return this;
	}
	
	@Override
	protected void logSuccess(Void value){
		log.info("Edited channel {}", channel);
	}
	
	@Override
	protected void logException(Throwable throwable){
		log.error("Failed to edit channel {}", channel, throwable);
	}
}
