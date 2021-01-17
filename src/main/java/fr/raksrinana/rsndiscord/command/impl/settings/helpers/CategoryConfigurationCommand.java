package fr.raksrinana.rsndiscord.command.impl.settings.helpers;

import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.settings.types.CategoryConfiguration;
import lombok.NonNull;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import static java.util.Optional.ofNullable;

public abstract class CategoryConfigurationCommand extends ValueConfigurationCommand<CategoryConfiguration>{
	protected CategoryConfigurationCommand(final Command parent){
		super(parent);
	}
	
	@Override
	protected CategoryConfiguration extractValue(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		if(args.isEmpty()){
			throw new IllegalArgumentException("Please mention the role");
		}
		return ofNullable(event.getGuild().getCategoryById(args.pop()))
				.map(CategoryConfiguration::new)
				.orElseThrow(() -> new IllegalArgumentException("Invalid category id"));
	}
	
	@Override
	protected String getValueName(){
		return "Category";
	}
}