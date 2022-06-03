package fr.raksrinana.rsndiscord.utils.jda.wrappers.channel;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.managers.channel.concrete.TextChannelManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Log4j2
public class EditTextChannelWrapper extends EditChannelWrapper{
	private final TextChannel channel;
	private TextChannelManager action;
	
	public EditTextChannelWrapper(@NotNull TextChannel channel){
		super(channel);
		
		this.channel = channel;
		action = channel.getManager();
	}
	
	@NotNull
	public EditTextChannelWrapper sync(@NotNull Category category){
		log.info("Will sync permissions of {} with {}", channel, category);
		action = action.sync(category);
		return this;
	}
	
	@NotNull
	public EditTextChannelWrapper setParent(@Nullable Category category){
		log.info("Will set {} in category {}", channel, category);
		action = action.setParent(category);
		return this;
	}
}
